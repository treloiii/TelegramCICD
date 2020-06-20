package com.trelloiii.cibot.dto.message.branch;

import com.trelloiii.cibot.dto.pipeline.PipelineUtils;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.trelloiii.cibot.dto.pipeline.PipelineRedactor.*;

@Component
public class MessageBranch extends AbstractBranch {
    private final PipelineService pipelineService;
    private final PipelineUtils callbackUtils;
    private final PipelineRedactor redactor;

    @Autowired
    public MessageBranch(PipelineService pipelineService, PipelineUtils callbackUtils, PipelineRedactor redactor) {
        this.pipelineService = pipelineService;
        this.callbackUtils = callbackUtils;
        this.redactor = redactor;
    }

    @Override
    public void process(Message message) {
        String chatId = message.getChatId().toString();
        String messageText = message.getText();
        switch (messageText) {
            case CREATE_PIPELINE:
                send(createPipeline(chatId));
                break;
            case SHOW_PIPELINES:
                showPipelines(message);
                break;
            case HELP:
                helpSend(chatId);
                break;
            default:
                redactOrDefault(messageText, Long.valueOf(chatId));
        }
    }

    private void helpSend(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableMarkdown(true);
        message.setText(
                "Hello its a simple *CI/CD bot*\n" +
                        "This bot can make simple builds and deployment\n" +
                        "To know full info, please [visit docs page](https://botinfo.trelloiii.site)");//TODO сменить урл если надо будет
        send(message);
    }

    private void redactOrDefault(String field, Long chatId) {
        try {
            if (redactor.getRedact()) { //if redact mod on
                if (redactor.checkField()) { //if user already pick field
                    redactor.redact(field);
                    send(mainProcess(chatId, "Successfully changed " + redactor.getField()));
                    redactor.clear();
                } else { //if user dont pick value
                    if (names.contains(field)) { //if user send correct field name
                        redactor.setField(field);
                        send(new SendMessage(chatId, "Now enter new value"));
                    } else { //if user send incorrect field name
                        send(mainProcess(chatId, "*Wrong value*"));
                    }
                }
            } else { //if redact mod off
                send(mainProcess(chatId, "What can I help you?"));
            }
        }
        catch (NumberFormatException e){
            send(mainProcess(chatId,"*Wrong number format for timer.Please verify what you pass integer number*"));
        }
    }

    @Override
    public void processCallback(Message message, String[] data) {
        String messageText = data[0];
        String pipelineId = data[1];
        Long chatId = message.getChatId();
        switch (messageText) {
            case "start":
                callbackUtils.startPipeline(pipelineId, chatId, getSendMessageFunction());
                break;
            case "history":
                callbackUtils.getHistory(pipelineId, chatId, getSendMessageFunction());
                break;
            case "delete":
                pipelineService.removePipeline(Long.valueOf(pipelineId));
                send(new SendMessage(chatId, "pipeline successfully deleted!"));
                break;
            case "redact":
                redactPipeline(pipelineId, chatId);
                break;
            default:
                send(new SendMessage(chatId, "delete"));
        }
    }

    private void redactPipeline(String pipelineId, Long chatId) {
        SendMessage message = new SendMessage(chatId, "pick what you want to change");
        message.enableMarkdown(true);
        setOneRowButtons(message, "name", "repository name", "token", "vcs branch", "timer");
        redactor.setPipelineId(pipelineId);
        redactor.setRedact(true);
        send(message);
    }

    private void showPipelines(Message tmMessage) {
        List<Pipeline> pipelineList = pipelineService.getPipelines();
        pipelineList.forEach(pipeline -> {
            String secured = "You're not a creator of this pipeline.\nToken is hidden";
            if (pipeline.getOwner().getId().equals(tmMessage.getFrom().getId())) {
                secured = pipeline.getOauthToken();
            }
            SendMessage message = new SendMessage(
                    tmMessage.getChatId(),
                    String.format(
                            "*%s*:\n `repository:` %s\n `token:` %s\n `vcs branch: `%s\n `timer: `%s",
                            pipeline.getName(),
                            pipeline.getRepositoryName(),
                            secured,
                            Optional.ofNullable(pipeline.getBranch()).orElse("not set"),
                            String.valueOf(pipeline.getTimer()).equals("null") ? "not set" : String.valueOf(pipeline.getTimer()) // pizdec idiotstvo
                    )
            );
            message.enableMarkdown(true);
            pipelineInline(message, pipeline);
            send(message);
        });
        if (pipelineList.isEmpty()) {
            SendMessage message = new SendMessage(tmMessage.getChatId(), "*Pipeline list is empty*".toUpperCase());
            message.enableMarkdown(true);
            send(message);
        }
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
