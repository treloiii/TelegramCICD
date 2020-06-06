package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import com.trelloiii.cibot.dto.pipeline.instruction.Instruction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;

import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;

@Data
@AllArgsConstructor
public class NativeUnixInstruction implements Instruction {
    private String text;
    private String directory;
    private Boolean status;

    public NativeUnixInstruction(String text, String directory) {
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
//        Process p = Runtime.getRuntime().exec(
//                text.split(" "), //cmd
//                null,
//                new File(directory));
        String [] arr=text.split(" ");
        arr[arr.length-1]+=" \\;";
        System.out.println(new ProcessExecutor().readOutput(true).command(arr).execute().getOutput().getUTF8());// in this dir run cmd
//        try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(p.getErrorStream()))){
//            String err;
//            while ((err=bufferedReader.readLine())!=null){
//                System.out.println(err);
//            }
//        }
    }
}
