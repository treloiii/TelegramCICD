package com.trelloiii.cibot.model.pipeline;
import com.trelloiii.cibot.dto.logger.LogExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.Arrays;

import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;

@Data
@AllArgsConstructor
public class Instruction {
    private String text;
    private String directory;
    private Boolean status;
    public Instruction(String text,String directory) {
        this.text = text;
        this.directory=directory;
    }

    public void execute(LogExecutor logExecutor){
        try {
            Process p = Runtime.getRuntime().exec(
                    text.split(" "), //cmd
                    null,
                    new File(directory));// in this dir run cmd
            readLog(p.getErrorStream(),logExecutor,true);
            readLog(p.getInputStream(),logExecutor,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
