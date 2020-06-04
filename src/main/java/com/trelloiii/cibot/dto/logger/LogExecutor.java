package com.trelloiii.cibot.dto.logger;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class LogExecutor {
    private Consumer<SendMessage> messageConsumer;
    private String chatId;
    public LogExecutor(Consumer<SendMessage> messageConsumer, String chatId) {
        this.messageConsumer = messageConsumer;
        this.chatId = chatId;
    }
    public void sendLog(String log) throws InterruptedException {
        SendMessage sendMessage = new SendMessage(this.chatId, log);
        sendMessage.enableMarkdown(true);
        Thread.sleep(250);
        messageConsumer.accept(sendMessage);
    }
}
