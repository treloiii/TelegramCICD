package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import com.trelloiii.cibot.dto.pipeline.instruction.Instruction;
import lombok.*;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;

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
            Process p = Runtime.getRuntime().exec(
                    text.split(" "), //cmd
                    null,
                    new File(directory));// in this dir run cmd

            readLog(p.getInputStream(), logExecutor, false);
            readLog(p.getErrorStream(), logExecutor, true);
            int code = p.exitValue();
            status = code == 0;
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
        Process p = Runtime.getRuntime().exec(
                    text.split(" "), //cmd
                    null,
                    new File(directory));
//        String[] arr = text.split(" ");
//        arr[arr.length - 1] += " \\;";
//        System.out.println(new ProcessExecutor().readOutput(true).command(arr).execute().getOutput().getUTF8());// in this dir run cmd
    }
}
