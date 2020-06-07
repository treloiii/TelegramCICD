package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.vcs.VCSCloner;
import com.trelloiii.cibot.exceptions.BuildFileNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.model.PipelineHistory;
import com.trelloiii.cibot.service.PipelineHistoryService;
import com.trelloiii.cibot.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
@Component
public class CallBackUtils {

    private final PipelineHistoryService pipelineHistoryService;
    private final PipelineService pipelineService;

    public CallBackUtils(PipelineHistoryService pipelineHistoryService, PipelineService pipelineService) {
        this.pipelineHistoryService = pipelineHistoryService;
        this.pipelineService = pipelineService;
    }

    public List<SendMessage> startPipeline(String data, String chatId, Consumer<SendMessage> sendMessageConsumer) {
        Pipeline pipeline=pipelineService.getPipeline(data);
        VCSCloner vcsCloner=new VCSCloner(pipeline.getOauthToken(),pipeline.getRepositoryName());
        vcsCloner.cloneRepos();
        //^parse vcs

        PipelineYamlParser parser = new PipelineYamlParser(pipeline);
        try {
            pipeline = parser.parse();
        }
        catch (BuildFileNotFoundException e){
            vcsCloner.removeRepos();
            return Collections.singletonList(new SendMessage(chatId,e.getMessage()));
        }
        pipelineService.execute(generateLoggable(chatId, pipeline,sendMessageConsumer));
        return Collections.singletonList(
                new SendMessage(
                        chatId,
                        String.format("Pipeline with id %s started!", data)
                )
        );
    }
    public List<SendMessage> getHistory(String data,String chatId){
        Pipeline pipeline=pipelineService.getPipeline(data);
        List<PipelineHistory> pipelineHistory=pipelineHistoryService.getHistoryByPipeline(pipeline);
        SendMessage sendMessage=new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        String head=String.format("_history of %s_",pipeline.getName());
        if(pipelineHistory.size()>0) {
            String tableHat=String.join(" | ",
                    fixedString("Executed at"," "),
                    fixedString("Status"," "),
                    fixedString("Failed stage"," "),
                    fixedString("Failed command"," "));
            String tableDelimeter=fixedString("","-");
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(head).append("\n");
            stringBuilder.append(tableHat).append("\n").append(tableDelimeter).append("\n");
            for(PipelineHistory history:pipelineHistory){
                stringBuilder.append(String.join(
                        " | ",
                        fixedString(history.getExecutedAt().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"))," "),
                        fixedString(!history.getStatus() ? "success" : "failed"," "),
                        fixedString(history.getFailed_stage()==null?"":history.getFailed_stage()," "),
                        fixedString(history.getFailed_instruction()==null?"":history.getFailed_instruction()," ")
                ))
                        .append("\n");
            }
            sendMessage.setText(stringBuilder.toString());
        }
        else{
            sendMessage.setText(head+"\n"+"*EMPTY*");
        }
        return Collections.singletonList(sendMessage);
    }
    private LoggablePipeline generateLoggable(String chatId, Pipeline pipeline, Consumer<SendMessage> sendMessageConsumer) {
        LoggablePipeline loggablePipeline = new LoggablePipeline();
        loggablePipeline.setId(chatId);
        loggablePipeline.setPipeline(pipeline);
        loggablePipeline.setSendMessageConsumer(sendMessageConsumer);
        return loggablePipeline;
    }
    private String fixedString(String s,String joiner){
        StringBuilder sb=new StringBuilder();
        sb.append(s);
        int len=joiner.equals(" ")?50:150;
        if (s.length()<len) {
            for (int i = 0; i < len-s.length(); i++) {
                sb.append(joiner);
            }
        }
        return sb.toString();
    }
}
