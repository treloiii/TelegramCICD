package com.trelloiii.cibot.dto.logger;

import com.trelloiii.cibot.model.Pipeline;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Consumer;
import java.util.function.Function;

public class Logger extends AbstractLogger {
    private final Function<Object, Message> sendMessageFunction;
    private final String chatId;
    private SendMessage sendMessage = null;
    private Message message = null;
    private final long time = System.currentTimeMillis();
    private boolean error = true;

    public Logger(Function<Object, Message> sendMessageFunction, String chatId, Pipeline pipeline) {
        super(pipeline);
        this.sendMessageFunction = sendMessageFunction;
        this.chatId = chatId;
    }

    public void sendLog(String log) {
//        writeLogToFile(log);
        if (sendMessage == null) {
            sendMessage = new SendMessage(this.chatId, log);
            message = sendMessageFunction.apply(sendMessage);
        } else {
            if (message != null) {
                if (System.currentTimeMillis()-time < 90*1000) {
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(chatId);
                    editMessageText.setText(log);
                    editMessageText.setMessageId(message.getMessageId());
                    sendMessageFunction.apply(editMessageText);
                } else {
                    if (error) {
                        sendMessageFunction.apply(new SendMessage(this.chatId, "Too much logs.\n" +
                                "Skip others excluding errors for performance.\n" +
                                "You can find full logs of build on the server"));
                        error = false;
                    }
                }
            }
        }
    }

    @Override
    public void sendForceLog(String log) {
        SendMessage sendMessage = new SendMessage(this.chatId, log);
        sendMessage.enableMarkdown(true);
        sendMessageFunction.apply(sendMessage);
    }

    @Override
    public void sendLogFile() {
        SendDocument sendDocument=new SendDocument();
        sendDocument.setDocument(getLogFile());
        sendDocument.setChatId(chatId);
        sendMessageFunction.apply(sendDocument);
    }

}
