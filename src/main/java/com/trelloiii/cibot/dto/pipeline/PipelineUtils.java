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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@Log
public class PipelineUtils {

    private final PipelineHistoryService pipelineHistoryService;
    private final PipelineService pipelineService;

    public PipelineUtils(PipelineHistoryService pipelineHistoryService, PipelineService pipelineService) {
        this.pipelineHistoryService = pipelineHistoryService;
        this.pipelineService = pipelineService;
    }

    public void startPipeline(String data, Long chatId, Function<Object, Message> sendMessageFunction) {
       VCSCloner vcsCloner=null;
        try {
            Pipeline pipeline = pipelineService.getPipeline(data);
            vcsCloner = new VCSCloner(pipeline.getOauthToken(), pipeline.getRepositoryName());
            sendMessageFunction.apply(new SendMessage(chatId,"Checkout VCS..."));
            vcsCloner.cloneRepos();
        //^parse vcs

            PipelineYamlParser parser = new PipelineYamlParser(pipeline);

            pipeline = parser.parse();
            pipelineService.execute(generateLoggable(chatId.toString(), pipeline,sendMessageFunction));
            sendMessageFunction.apply(new SendMessage(chatId, String.format("Pipeline with id %s started!", data)));
        }
        catch (PipelineNotFoundException e){
            sendMessageFunction.apply(new SendMessage(chatId,"This pipeline not found"));
        }
        catch (BuildFileNotFoundException | EnvironmentNotFoundException | GithubAuthException | GithubRepositoryNotFoundException e){
            Optional.ofNullable(vcsCloner).ifPresent(VCSCloner::removeRepos);
            sendMessageFunction.apply(new SendMessage(chatId,e.getMessage()+"\nBuild will be terminated"));
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
    public void getHistory(String pipelineId, Long chatId, Function<Object, Message> sendMessage){
        Pipeline pipeline=pipelineService.getPipeline(pipelineId);
        List<PipelineHistory> pipelineHistory=pipelineHistoryService.getHistoryByPipeline(pipeline);
        SendMessage message=new SendMessage();
        message.setChatId(chatId);
        String head=String.format("HISTORY OF %s\n",pipeline.getName());
        if(pipelineHistory.size()>0) {
            StringBuilder stringBuilder=new StringBuilder(head);
            for(PipelineHistory history:pipelineHistory){
                String execAt=String.format("EXECUTED AY: %s",history.getExecutedAt());
                String status=String.format("STATUS: %s",history.getStatus());
                stringBuilder.append(execAt).append("\n")
                        .append(status).append("\n");
                if(!history.getStatus()){
                    String failedStage=String.format("FAILED STAGE: %s",history.getFailed_stage());
                    String failedCommand=String.format("FAILED COMMAND: %s",history.getFailed_instruction());
                    stringBuilder.append(failedStage).append("\n")
                            .append(failedCommand).append("\n");
                }
                stringBuilder.append("_____\n");
            }
            message.setText(stringBuilder.toString());
        }
        else{
            message.enableMarkdown(true);
            message.setText(head+"\n"+"*EMPTY*");
        }
        sendMessage.apply(message);
    }
    private LoggablePipeline generateLoggable(String chatId, Pipeline pipeline, Function<Object, Message> sendMessageFunction) {
        LoggablePipeline loggablePipeline = new LoggablePipeline();
        loggablePipeline.setId(chatId);
        loggablePipeline.setPipeline(pipeline);
        loggablePipeline.setSendMessageFunction(sendMessageFunction);
        loggablePipeline.initLogger();
        return loggablePipeline;
    }

    private void cloneAndExecute(VCSCloner cloner,Pipeline pipeline) throws EnvironmentNotFoundException {
        cloner.cloneRepos();
        PipelineYamlParser parser = new PipelineYamlParser(pipeline);
        pipeline = parser.parse();
        pipelineService.execute(new QuietPipeline(pipeline, new QuietLogger(pipeline)));
    }
}
