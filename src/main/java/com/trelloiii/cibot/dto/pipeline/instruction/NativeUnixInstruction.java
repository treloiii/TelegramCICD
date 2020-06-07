package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import com.trelloiii.cibot.dto.pipeline.instruction.Instruction;
import lombok.*;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.*;
import java.util.stream.Stream;

import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NativeUnixInstruction implements Instruction {
    private String text;
    private String directory;
    private Boolean status;
    private Boolean ignoreOnExit=false;

    public NativeUnixInstruction(String text, String directory) {
        this.text = text;
        this.directory = directory;
    }

    public NativeUnixInstruction(String text, String directory,Boolean ignoreOnExit) {
        this.text = text;
        this.directory = directory;
        this.ignoreOnExit = ignoreOnExit;
    }

    public int execute(LogExecutor logExecutor) {
        try {
            ProcessResult res=new ProcessExecutor(text.split(" "))
                    .directory(new File(directory))
                    .readOutput(true)
                    .execute();
            int code=res.getExitValue();
            status = code == 0;
            readLog(res.getOutput().getLines(),logExecutor,!status);
            if(ignoreOnExit){
                status=true;
                code=0;
            }
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @SneakyThrows
    public void execute() {
        new ProcessExecutor(text.split(" ")) //cmd
                .directory(new File(directory)) // in this dir we run cmd
                .execute();
    }
}
