package com.trelloiii.cibot.dto.message.branch;

import com.trelloiii.cibot.dto.message.branch.AbstractBranch;
import com.trelloiii.cibot.dto.pipeline.CallBackUtils;
import com.trelloiii.cibot.dto.pipeline.PipelineFactory;
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

@Component
public class MessageBranch extends AbstractBranch {
    private final PipelineService pipelineService;
    private final CallBackUtils callbackUtils;
    @Autowired
    public MessageBranch(PipelineService pipelineService, CallBackUtils callbackUtils) {
        this.pipelineService = pipelineService;
        this.callbackUtils = callbackUtils;
    }

    @Override
    public void process(Message message,Consumer<SendMessage> sendMessage) {
        String chatId=message.getChatId().toString();
        String messageText=message.getText();
        switch (messageText){
            case "Create pipeline":
                sendMessage.accept(createPipeline(chatId));
                break;
            case "show my pipelines":
                showPipelines(chatId,sendMessage);
                break;
            case "main":
            case "start":
            default:
                sendMessage.accept(mainProcess(message.getChatId(),"What can I help you?"));
        }
    }

    @Override
    public void processCallback(Message message, String[] data,Consumer<SendMessage> sendMessage) {
        String messageText=data[0];
        String pipelineId=data[1];
        Long chatId=message.getChatId();
        switch (messageText){
            case "start":
                callbackUtils.startPipeline(pipelineId, chatId, sendMessage);
                break;
            case "history":
                callbackUtils.getHistory(pipelineId,chatId,sendMessage);
                break;
            case "delete":
                pipelineService.removePipeline(Long.valueOf(pipelineId));
            default:
                sendMessage.accept(new SendMessage(chatId, "delete"));
        }
    }

    private void showPipelines(String chatId, Consumer<SendMessage> sendMessage) {
        List<Pipeline> pipelineList=pipelineService.getPipelines();
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
            pipelineInline(message,pipeline);
            sendMessage.accept(message);
        });
    }

    @SneakyThrows
    private void pipelineInline(SendMessage sendMessage, Pipeline pipeline) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(new InlineKeyboardButton().setText("start")
                .setCallbackData("msg&start&"+pipeline.getId().toString()));
        buttons1.add(new InlineKeyboardButton().setText("delete")
                .setCallbackData("msg&delete&"+pipeline.getId().toString()));
        buttons1.add(new InlineKeyboardButton().setText("history")
                .setCallbackData("msg&history&"+pipeline.getId().toString()));
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
