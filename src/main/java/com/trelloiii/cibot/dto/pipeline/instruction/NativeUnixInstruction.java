package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.dto.logger.Logger;
import com.trelloiii.cibot.dto.logger.LoggerUtils;
import lombok.*;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;
import reactor.core.publisher.Flux;

import java.io.*;
import java.time.Duration;

import static com.trelloiii.cibot.dto.logger.LoggerUtils.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NativeUnixInstruction implements Instruction {
    private String text;
    private String directory;
    private Boolean status;
    private Boolean ignoreOnExit = false;
    private int limit=3;
    public NativeUnixInstruction(String text, String directory) {
        this.text = text;
        this.directory = directory;
    }

    public NativeUnixInstruction(String text, String directory, Boolean ignoreOnExit) {
        this.text = text;
        this.directory = directory;
        this.ignoreOnExit = ignoreOnExit;
    }

    public int execute(AbstractLogger logger) {
        try {
//            readLog(String.format("starting %s",text),logger,false);
            System.out.println(text);
            ProcessResult res = new ProcessExecutor(text.split(" "))
                    .directory(new File(directory))
                    .readOutput(true)
                    .redirectError(new LogOutputStream() {
                        @Override
                        protected void processLine(String s) {
                            readLog(s, logger, true);
                        }
                    })
                    .redirectOutput(new LogOutputStream(){
                        @Override
                        protected void processLine(String s) {
                            readFileLog(s, logger);
                            if(limit>0) {
                                limit--;
                            }else{
                                readLog(s, logger, false);
                                limit=3;
                            }
                        }
                    })
                    .execute();
            readLast(logger,false);
            int code = res.getExitValue();
            status = code == 0;
//            readLog(String.format("Executing %s please wait...",text),logger,false);
//            readLog(res.getOutput().getLines(),logger, !status);
            //ТУТ МЫ СОСЕМ НА БЛОКИРОВКЕ, ПРИЧЕМ КОНКРЭТНО ТАК СОСЕМ, ОЧЕНЬ ДОЛГО СОСЕМ
            if (ignoreOnExit) {
                status = true;
                code = 0;
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
