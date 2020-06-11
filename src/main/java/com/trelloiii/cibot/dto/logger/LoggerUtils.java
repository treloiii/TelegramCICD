package com.trelloiii.cibot.dto.logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LoggerUtils {
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
        sendLog(log, logger,isError);
    }
    public static void readLog(List<String> logs, AbstractLogger logger, boolean isError){
        logs.forEach(log->sendLog(log, logger,isError));
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
}
