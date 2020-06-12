package com.trelloiii.cibot.dto.logger;

import com.google.common.hash.Hashing;
import com.trelloiii.cibot.model.Pipeline;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
        LocalDateTime nowTime=LocalDateTime.now(ZoneId.systemDefault());
        return String.format("build_log_%s_%d_%d_%d_%d_%d.log",
                pipeline.getName(),
                nowTime.getYear(),
                nowTime.getMonthValue(),
                nowTime.getDayOfMonth(),
                nowTime.getHour(),
                nowTime.getMinute());
    }
    public abstract void sendLog(String log,String rawLog) throws InterruptedException;
}
