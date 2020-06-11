package com.trelloiii.cibot.dto.logger;

import com.google.common.hash.Hashing;
import com.trelloiii.cibot.model.Pipeline;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public abstract class AbstractLogger {
    public final Pipeline pipeline;
    public final File logFile;

    protected AbstractLogger(Pipeline pipeline) {
        this.pipeline = pipeline;
        logFile = new File(filename());
        pipeline.setLogPath(logFile.getAbsolutePath());
    }

    public synchronized void writeLogToFile(String log){
        try(FileWriter fileWriter=new FileWriter(logFile,true)){
            fileWriter.write(log+"\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SneakyThrows
    public String filename() {
        String timestamp=String.valueOf(System.currentTimeMillis());
        String encodedTimestamp= Hashing
                .sha256()
                .hashString(timestamp, StandardCharsets.UTF_8)
                .toString();
        return String.format("log_pipeline_%s_%s_%s.log",pipeline.getId(),pipeline.getName(),encodedTimestamp);
    }
    public abstract void sendLog(String log,String rawLog) throws InterruptedException;
}
