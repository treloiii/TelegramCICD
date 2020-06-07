package com.trelloiii.cibot.dto.logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LoggerUtils {
    public static void readLog(InputStream inputStream, LogExecutor logExecutor, boolean isError) {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String error;
            while ((error = errorReader.readLine()) != null) {
                sendLog(error, logExecutor, isError);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void readLog(String log,LogExecutor logExecutor,boolean isError){
        sendLog(log,logExecutor,isError);
    }
    public static void readLog(List<String> logs,LogExecutor logExecutor,boolean isError){
        logs.forEach(log->sendLog(log,logExecutor,isError));
    }
    public static void sendLog(String output, LogExecutor logExecutor, boolean isError) {
        try {
            if (!output.isEmpty()) {
                if (!isError)
                    logExecutor.sendLog(String.format("*[LOG]*: `%s`", output));
                else
                    logExecutor.sendLog(String.format("*[ERROR]*: `%s`", output));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
