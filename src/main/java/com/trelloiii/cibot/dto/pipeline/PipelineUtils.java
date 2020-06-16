package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.QuietLogger;
import com.trelloiii.cibot.dto.vcs.GithubHook;
import com.trelloiii.cibot.dto.vcs.VCSCloner;
import com.trelloiii.cibot.exceptions.*;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.model.PipelineHistory;
import com.trelloiii.cibot.service.PipelineHistoryService;
import com.trelloiii.cibot.service.PipelineService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@Log
public class PipelineUtils {

    private final PipelineHistoryService pipelineHistoryService;
    private final PipelineService pipelineService;

    public PipelineUtils(PipelineHistoryService pipelineHistoryService, PipelineService pipelineService) {
        this.pipelineHistoryService = pipelineHistoryService;
        this.pipelineService = pipelineService;
    }

    public void startPipeline(String data, Long chatId, Consumer<SendMessage> sendMessageConsumer) {
       VCSCloner vcsCloner=null;
        try {
            Pipeline pipeline = pipelineService.getPipeline(data);
            vcsCloner = new VCSCloner(pipeline.getOauthToken(), pipeline.getRepositoryName());
            sendMessageConsumer.accept(new SendMessage(chatId,"Checkout VCS..."));
            vcsCloner.cloneRepos();
        //^parse vcs

            PipelineYamlParser parser = new PipelineYamlParser(pipeline);

            pipeline = parser.parse();
            pipelineService.execute(generateLoggable(chatId.toString(), pipeline,sendMessageConsumer));
            sendMessageConsumer.accept(new SendMessage(chatId, String.format("Pipeline with id %s started!", data)));
        }
        catch (PipelineNotFoundException e){
            sendMessageConsumer.accept(new SendMessage(chatId,"This pipeline not found"));
        }
        catch (BuildFileNotFoundException | EnvironmentNotFoundException | GithubAuthException | GithubRepositoryNotFoundException e){
            Optional.ofNullable(vcsCloner).ifPresent(VCSCloner::removeRepos);
            sendMessageConsumer.accept(new SendMessage(chatId,e.getMessage()+"\nBuild will be terminated"));
        }
    }
    public void startPipelineQuiet(GithubHook hook){
        VCSCloner vcsCloner=null;
        try{
            Pipeline pipeline=pipelineService.getPipelineByReposName(hook.getRepository());
            if(pipeline.getBranch()==null||pipeline.getBranch().equals(hook.getBranch())) {
                vcsCloner = new VCSCloner(pipeline.getOauthToken(), pipeline.getRepositoryName());
                cloneAndExecute(vcsCloner,pipeline);
            }
        }
        catch (Exception e){
            Optional.ofNullable(vcsCloner).ifPresent(VCSCloner::removeRepos);
            e.printStackTrace();
        }
    }
    public void startPipelineQuiet(Pipeline pipeline){
        VCSCloner vcsCloner=null;
        try{
            vcsCloner = new VCSCloner(pipeline.getOauthToken(), pipeline.getRepositoryName());
            cloneAndExecute(vcsCloner,pipeline);
        }catch (Exception e){
            Optional.ofNullable(vcsCloner).ifPresent(VCSCloner::removeRepos);
            e.printStackTrace();
        }
    }
    public void getHistory(String pipelineId, Long chatId, Consumer<SendMessage> sendMessage){
        Pipeline pipeline=pipelineService.getPipeline(pipelineId);
        List<PipelineHistory> pipelineHistory=pipelineHistoryService.getHistoryByPipeline(pipeline);
        SendMessage message=new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        String head=String.format("_history of %s_",pipeline.getName());
        if(pipelineHistory.size()>0) {
            String tableHat=String.join(" | ",
                    fixedString("Executed at"," "),
                    fixedString("Status"," "),
                    fixedString("Failed stage"," "),
                    fixedString("Failed command"," "));
            String tableDelimiter=fixedString("","-");
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(head).append("\n");
            stringBuilder.append(tableHat).append("\n").append(tableDelimiter).append("\n");
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
            message.setText(stringBuilder.toString());
        }
        else{
            message.setText(head+"\n"+"*EMPTY*");
        }
        sendMessage.accept(message);
    }
    private LoggablePipeline generateLoggable(String chatId, Pipeline pipeline, Consumer<SendMessage> sendMessageConsumer) {
        LoggablePipeline loggablePipeline = new LoggablePipeline();
        loggablePipeline.setId(chatId);
        loggablePipeline.setPipeline(pipeline);
        loggablePipeline.setSendMessageConsumer(sendMessageConsumer);
        loggablePipeline.initLogger();
        return loggablePipeline;
    }

    private void cloneAndExecute(VCSCloner cloner,Pipeline pipeline) throws EnvironmentNotFoundException {
        cloner.cloneRepos();
        PipelineYamlParser parser = new PipelineYamlParser(pipeline);
        pipeline = parser.parse();
        pipelineService.execute(new QuietPipeline(pipeline, new QuietLogger(pipeline)));
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
