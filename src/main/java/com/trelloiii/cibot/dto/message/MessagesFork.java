package com.trelloiii.cibot.dto.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trelloiii.cibot.dto.pipeline.LoggablePipeline;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.dto.pipeline.PipelineYamlParser;
import com.trelloiii.cibot.dto.vcs.VCSCloner;
import com.trelloiii.cibot.exceptions.BuildFileNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Component
public class MessagesFork extends AbstractFork {
    private final PipelineService pipelineService;
    final ObjectMapper objectMapper;
    @Autowired
    public MessagesFork(PipelineService pipelineService, ObjectMapper objectMapper) {
        this.pipelineService = pipelineService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<SendMessage> process(String message, String chatId) {
        switch (message){
            case "Create pipeline":
                return Collections.singletonList(createPipeline(chatId));
            case "show my pipelines":
                return showPipelines(chatId);
            case "main":
            case "start":
            default:
                return mainProcess(chatId,"What can I help you?");
        }
    }

    @Override
    public List<SendMessage> processCallback(String message, String data, String chatId, Consumer<SendMessage> sendMessageConsumer) {
        switch (message){
            case "start":
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
            case "delete":
            default:
                return Collections.singletonList(
                        new SendMessage(
                                chatId,
                                "delete"
                        )
                );
        }
    }
    private LoggablePipeline generateLoggable(String chatId, Pipeline pipeline,Consumer<SendMessage> sendMessageConsumer) {
        LoggablePipeline loggablePipeline = new LoggablePipeline();
        loggablePipeline.setId(chatId);
        loggablePipeline.setPipeline(pipeline);
        loggablePipeline.setSendMessageConsumer(sendMessageConsumer);
        return loggablePipeline;
    }
    private List<SendMessage> showPipelines(String chatId) {
        List<Pipeline> pipelineList=pipelineService.getPipelines();
        List<SendMessage> result=new ArrayList<>();
        for(Pipeline pipeline:pipelineList){
            SendMessage sendMessage=
                    new SendMessage(
                            chatId,
                            String.format(
                                    "%s: [repository: %s , token: %s]",
                                    pipeline.getName(),
                                    pipeline.getRepositoryName(),
                                    pipeline.getOauthToken()
                            )
                    );
            pipelineInline(sendMessage,pipeline);
            result.add(sendMessage);
        }
        return result;
    }

    @SneakyThrows
    private void pipelineInline(SendMessage sendMessage, Pipeline pipeline) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(new InlineKeyboardButton().setText("start")
                .setCallbackData("msg&start&"+pipeline.getId().toString()));
        buttons1.add(new InlineKeyboardButton().setText("delete")
                .setCallbackData("msg&delete&"+pipeline.getId().toString()));
        buttons.add(buttons1);
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        sendMessage.setReplyMarkup(markupKeyboard);
    }


    private SendMessage createPipeline(String chatId) {
        PipelineFactory.instance();
        SendMessage sendMessage=new SendMessage(chatId,"Lets define name of a pipeline");
        setOneRowButtons(sendMessage,"Main","One stage back <--");
        return sendMessage;
    }
}
