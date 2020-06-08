package com.trelloiii.cibot.dto.message.branch;

import com.trelloiii.cibot.dto.pipeline.CallBackUtils;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
import com.trelloiii.cibot.dto.pipeline.PipelineRedactor;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MessageBranch extends AbstractBranch {
    private final PipelineService pipelineService;
    private final CallBackUtils callbackUtils;
    private final PipelineRedactor redactor;

    @Autowired
    public MessageBranch(PipelineService pipelineService, CallBackUtils callbackUtils, PipelineRedactor redactor) {
        this.pipelineService = pipelineService;
        this.callbackUtils = callbackUtils;
        this.redactor = redactor;
    }

    @Override
    public void process(Message message, Consumer<SendMessage> sendMessage) {
        String chatId = message.getChatId().toString();
        String messageText = message.getText();
        switch (messageText) {
            case "Create pipeline":
                sendMessage.accept(createPipeline(chatId));
                break;
            case "show my pipelines":
                showPipelines(chatId, sendMessage);
                break;
            case "main":
            case "start":
            default:
                redactOrDefault(sendMessage,messageText,Long.valueOf(chatId));
        }
    }

    private void redactOrDefault(Consumer<SendMessage> sendMessage, String field, Long chatId) {
        if(redactor.getRedact()){
            if (redactor.checkField()) {
                redactor.redact(field);
                sendMessage.accept(mainProcess(chatId,"Successfully changed "+redactor.getField()));
                redactor.clear();
            }else{
                redactor.setField(field);
                sendMessage.accept(new SendMessage(chatId,"Now enter new value"));
            }
        }else {
            sendMessage.accept(mainProcess(chatId, "What can I help you?"));
        }
    }

    @Override
    public void processCallback(Message message, String[] data, Consumer<SendMessage> sendMessage) {
        String messageText = data[0];
        String pipelineId = data[1];
        Long chatId = message.getChatId();
        switch (messageText) {
            case "start":
                callbackUtils.startPipeline(pipelineId, chatId, sendMessage);
                break;
            case "history":
                callbackUtils.getHistory(pipelineId, chatId, sendMessage);
                break;
            case "delete":
                pipelineService.removePipeline(Long.valueOf(pipelineId));
                sendMessage.accept(new SendMessage(chatId, "pipeline successfully deleted!"));
                break;
            case "redact":
                redactPipeline(pipelineId, chatId, sendMessage);
                break;
            default:
                sendMessage.accept(new SendMessage(chatId, "delete"));
        }
    }

    private void redactPipeline(String pipelineId, Long chatId, Consumer<SendMessage> sendMessage) {
        SendMessage message = new SendMessage(chatId, "pick what you want to change");
        message.enableMarkdown(true);
        setOneRowButtons(message, "name","repository name","token");
        redactor.setPipelineId(pipelineId);
        redactor.setRedact(true);
        sendMessage.accept(message);
    }

    private void showPipelines(String chatId, Consumer<SendMessage> sendMessage) {
        List<Pipeline> pipelineList = pipelineService.getPipelines();
        pipelineList.forEach(pipeline -> {
            SendMessage message = new SendMessage(
                    chatId,
                    String.format(
                            "%s: [repository: %s , token: %s]",
                            pipeline.getName(),
                            pipeline.getRepositoryName(),
                            pipeline.getOauthToken()
                    )
            );
            pipelineInline(message, pipeline);
            sendMessage.accept(message);
        });
    }

    @SneakyThrows
    private void pipelineInline(SendMessage sendMessage, Pipeline pipeline) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = Stream.of("start", "delete", "history", "redact")
                .map(InlineKeyboardButton::new)
                .peek(k -> k.setCallbackData("msg&" + k.getText() + "&" + pipeline.getId()))
                .collect(Collectors.toList());
        buttons.add(buttons1);
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        sendMessage.setReplyMarkup(markupKeyboard);
    }


    private SendMessage createPipeline(String chatId) {
        PipelineFactory.instance();
        SendMessage sendMessage = new SendMessage(chatId, "Lets define name of a pipeline");
        setOneRowButtons(sendMessage, "Main", "One stage back <--");
        return sendMessage;
    }
}
