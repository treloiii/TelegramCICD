package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Arrays;

import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;

@Data
@AllArgsConstructor
public class Instruction {
    private String text;
    private String directory;
    private Boolean status;

    public Instruction(String text, String directory) {
        this.text = text;
        this.directory = directory;
    }

    public int execute(LogExecutor logExecutor) {
        try {
            Process p = Runtime.getRuntime().exec(
                    text.split(" "), //cmd
                    null,
                    new File(directory));// in this dir run cmd
            readLog(p.getErrorStream(), logExecutor, true);
            readLog(p.getInputStream(), logExecutor, false);
            int code = p.exitValue();
            status = code == 0;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    @SneakyThrows
    public void execute(){
        Process p = Runtime.getRuntime().exec(
                text.split(" "), //cmd
                null,
                new File(directory));// in this dir run cmd
        p.waitFor();
    }
}
