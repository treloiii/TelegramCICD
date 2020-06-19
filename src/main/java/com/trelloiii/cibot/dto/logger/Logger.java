package com.trelloiii.cibot.dto.logger;

import com.google.common.hash.Hashing;
import com.trelloiii.cibot.model.Pipeline;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class Logger extends AbstractLogger {
    private final Consumer<SendMessage> messageConsumer;
    private final String chatId;

    public Logger(Consumer<SendMessage> messageConsumer, String chatId, Pipeline pipeline) {
        super(pipeline);
        this.messageConsumer = messageConsumer;
        this.chatId = chatId;
    }

    public void sendLog(String log){
//        writeLogToFile(log);
        SendMessage sendMessage = new SendMessage(this.chatId, log);
        messageConsumer.accept(sendMessage);
    }
}
