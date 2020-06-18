package com.trelloiii.cibot.dto.logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LoggerUtils {
    static LogThread logThread = new LogThread();
    static {
        new Thread(logThread).start();
    }
    public static void readLog(InputStream inputStream, AbstractLogger logger, boolean isError) {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String error;
            while ((error = errorReader.readLine()) != null) {
                sendLog(error, logger, isError);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void readLog(String log, AbstractLogger logger, boolean isError){
//        sendLog(log,logger,isError);
        logThread.add(new LogWrapper(log, logger, isError));
    }
    public static void sendLog(String output, AbstractLogger logger, boolean isError) {
        try {
            if (!output.isEmpty()) {
                if (!isError)
                    logger.sendLog(String.format("*[LOG]*: `%s`", output),output);
                else
                    logger.sendLog(String.format("*[ERROR]*: `%s`", output),output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void sendLog(LogWrapper logWrapper){
        sendLog(logWrapper.getLog(),logWrapper.getLogger(),logWrapper.isError());
    }
    public static class LogThread implements Runnable{
        private BlockingQueue<LogWrapper> queue=new ArrayBlockingQueue<>(1000);
        @SneakyThrows
        public void add(LogWrapper s){
            queue.put(s);
        }
        @SneakyThrows
        @Override
        public void run() {
            while (true){
                sendLog(queue.take());
            }
        }
    }
    @Data
    @AllArgsConstructor
    public static class LogWrapper{
        String log;
        AbstractLogger logger;
        boolean isError;
    }
}
