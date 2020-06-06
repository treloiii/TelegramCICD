package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.vcs.VCSCloner;
import com.trelloiii.cibot.exceptions.BuildFileNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BuildStarter {
    public static List<SendMessage> start(PipelineService pipelineService, String data, String chatId, Consumer<SendMessage> sendMessageConsumer) {
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
    private static LoggablePipeline generateLoggable(String chatId, Pipeline pipeline, Consumer<SendMessage> sendMessageConsumer) {
        LoggablePipeline loggablePipeline = new LoggablePipeline();
        loggablePipeline.setId(chatId);
        loggablePipeline.setPipeline(pipeline);
        loggablePipeline.setSendMessageConsumer(sendMessageConsumer);
        return loggablePipeline;
    }
}
