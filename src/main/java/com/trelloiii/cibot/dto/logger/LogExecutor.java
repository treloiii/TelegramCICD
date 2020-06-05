package com.trelloiii.cibot.dto.logger;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class LogExecutor {
    private final Consumer<SendMessage> messageConsumer;
    private final String chatId;
    public LogExecutor(Consumer<SendMessage> messageConsumer, String chatId) {
        this.messageConsumer = messageConsumer;
        this.chatId = chatId;
    }
    public void sendLog(String log) throws InterruptedException {
        int size=100;
//        for(int i=0;i<log.length();i+=size) {
//            String send=log.substring(i,Math.min(log.length(),size));
            SendMessage sendMessage = new SendMessage(this.chatId, log);
            sendMessage.enableMarkdown(true);
            Thread.sleep(1000);
            messageConsumer.accept(sendMessage);
//        }
    }
}
