package com.trelloiii.cibot.dto.logger;

import com.google.common.hash.Hashing;
import com.trelloiii.cibot.Utils;
import com.trelloiii.cibot.model.Pipeline;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        File logs=new File(String.format("%s/logs",Utils.USER_DIST));//create logs folder
        if(!logs.exists())
            logs.mkdirs();
        return String.format("%s/%s/build_log_%s_%d_%d_%d_%d_%d.log",
                Utils.USER_DIST,
                "logs",
                pipeline.getName(),
                nowTime.getYear(),
                nowTime.getMonthValue(),
                nowTime.getDayOfMonth(),
                nowTime.getHour(),
                nowTime.getMinute());
    }
    public abstract void sendLog(String log) throws InterruptedException;
    public abstract void sendForceLog(String log);
    public void fileLog(String log) throws IOException{
        writeLogToFile(log);
    }
}
