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
import java.util.concurrent.CopyOnWriteArrayList;

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
        //logThread.add(new LogWrapper(log, logger, isError));
        logThread.push(log, logger, isError);
    }
    public static void readLast( AbstractLogger logger, boolean isError){
        logThread.pushLast(logger,isError);
    }
    public static void readLog(List<String> lines,AbstractLogger logger,boolean isError){
        List<String> grouped= group(lines);
        grouped.forEach(line->logThread.add(new LogWrapper(line,logger,isError)));
    }
    private static List<String> group(List<String> lines){
        int max=4096;
        List<String> res=new ArrayList<>();
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<lines.size();){
            String line=lines.get(i);
            if(line.length()<=max){
                builder.append(line).append("\n");
                max-=line.length();
                i++;
            }else{
                res.add(builder.toString());
                builder=new StringBuilder();
                max=4096;
            }
        }
        res.add(builder.toString());
        return res;
    }
    public static void readFileLog(String log,AbstractLogger logger){
        logger.writeLogToFile(log);
    }

    @SneakyThrows
    public static void sendLog(String output, AbstractLogger logger, boolean isError) {
        Thread.sleep(1000); //to avoid telegram restriction of rps
        try {
            if (!output.trim().isEmpty()) {
                if (!isError)
                    logger.sendLog(String.format("[INFO]: %s", output));
                else
                    logger.sendLog(String.format("[ERROR]: %s", output));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void sendLog(LogWrapper logWrapper){
        sendLog(logWrapper.getLog(),logWrapper.getLogger(),logWrapper.isError());
    }
    public static class LogThread implements Runnable{
        private final BlockingQueue<LogWrapper> queue=new ArrayBlockingQueue<>(1000);
        private CopyOnWriteArrayList<String> list=new CopyOnWriteArrayList<>();
        private int size=3800;
        @SneakyThrows
        public void add(LogWrapper s){
            queue.put(s);
        }
        @SneakyThrows
        public synchronized void push(String line, AbstractLogger logger, boolean isError){
            if(line.length()<=size){
                list.add(line+"\n");
                size-=line.length();
            }else{
                String res=list.stream().reduce("",String::concat);
                add(new LogWrapper(res,logger,isError));
                list=new CopyOnWriteArrayList<>();
                list.add(line+"\n");
                size=3800-line.length();
            }
        }
        public synchronized void pushLast(AbstractLogger logger, boolean isError){
            add(new LogWrapper(list.stream().reduce("",String::concat),logger,isError));
            list=new CopyOnWriteArrayList<>();
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
