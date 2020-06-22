package com.trelloiii.cibot.dto.pipeline.instruction;


import com.trelloiii.cibot.dto.logger.AbstractLogger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.trelloiii.cibot.dto.logger.LoggerUtils.readFileLog;
import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class JavaInstruction implements Instruction {
    String workingDir;
    String targetFile;
    Boolean status;
    Boolean ignoreOnExit;
    public void log(String log, AbstractLogger logger, boolean isError){
        readLog(log,logger,isError);
        readFileLog(log,logger);
    }

    //copy
    //delete
    //create
    //read
    //write
}
