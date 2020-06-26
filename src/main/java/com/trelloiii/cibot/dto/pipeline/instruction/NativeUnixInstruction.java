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
    private int limit = 3;

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
        ProcessExecutor executor;
        try {
            System.out.println(text);
            executor = new ProcessExecutor(text.split(" "))
                    .directory(new File(directory))
                    .readOutput(true)
                    .redirectError(new LogOutputStream() {
                        @Override
                        protected void processLine(String s) {
                            readFileLog(s, logger);
                            readLog(s, logger, true);
                        }
                    })
                    .redirectOutput(new LogOutputStream() {
                        @Override
                        protected void processLine(String s) {
                            readFileLog(s, logger);
                            readLog(s, logger, false);
                        }
                    })
                    .destroyOnExit();
            ProcessResult res = executor.execute();
            readLast(logger, false);
            int code = res.getExitValue();
            status = code == 0;
            if (ignoreOnExit) {
                status = true;
                code = 0;
            }
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            readFileLog(e.getMessage(), logger);
            readLog(e.getMessage(), logger, true);
            status=false;
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
