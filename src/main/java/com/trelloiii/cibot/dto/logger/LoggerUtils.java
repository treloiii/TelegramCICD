package com.trelloiii.cibot.dto.logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoggerUtils {
    public static void readLog(InputStream inputStream, LogExecutor logExecutor, boolean isError){
        try(BufferedReader errorReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String error;
            StringBuilder errorBuilder = new StringBuilder();
            while ((error = errorReader.readLine()) != null) {
                errorBuilder.append(error).append("\n");
            }
            sendLog(errorBuilder, logExecutor, isError);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void sendLog(StringBuilder builder,LogExecutor logExecutor,boolean isError){
        try {
            String output = builder.toString();
            if (!output.isEmpty()) {
                if(!isError)
                    logExecutor.sendLog(String.format("*[LOG]*: `%s`", output));
                else
                    logExecutor.sendLog(String.format("*[ERROR]*: `%s`", output));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
