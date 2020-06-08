package com.trelloiii.cibot.dto.logger;

import com.google.common.hash.Hashing;
import com.trelloiii.cibot.model.Pipeline;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.function.Consumer;

public class LogExecutor {
    private final Consumer<SendMessage> messageConsumer;
    private final String chatId;
    private final Pipeline pipeline;
    private final File logFile;

    public LogExecutor(Consumer<SendMessage> messageConsumer, String chatId, Pipeline pipeline) {
        this.messageConsumer = messageConsumer;
        this.chatId = chatId;
        this.pipeline = pipeline;
        logFile = new File(filename());
        pipeline.setLogPath(logFile.getAbsolutePath());
    }

    public void sendLog(String log,String rawLog) throws InterruptedException {
        writeLogToFile(rawLog);
        SendMessage sendMessage = new SendMessage(this.chatId, log);
        sendMessage.enableMarkdown(true);
        Thread.sleep(100);
        messageConsumer.accept(sendMessage);
    }
    private synchronized void writeLogToFile(String log){
        try(FileWriter fileWriter=new FileWriter(logFile,true)){
            fileWriter.write(log+"\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SneakyThrows
    private String filename() {
        String timestamp=String.valueOf(System.currentTimeMillis());
        String encodedTimestamp=Hashing
                .sha256()
                .hashString(timestamp,StandardCharsets.UTF_8)
                .toString();
        return String.format("log_pipeline_%s_%s_%s.log",pipeline.getId(),pipeline.getName(),encodedTimestamp);
    }
}
