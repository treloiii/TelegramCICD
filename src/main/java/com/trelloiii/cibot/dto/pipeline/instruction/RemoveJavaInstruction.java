package com.trelloiii.cibot.dto.pipeline.instruction;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.dto.logger.Logger;
import org.apache.commons.io.FileUtils;

import static com.trelloiii.cibot.dto.logger.LoggerUtils.readFileLog;
import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RemoveJavaInstruction extends JavaInstruction {
    public RemoveJavaInstruction(String targetFile, Boolean ignoreOnExit) {
        super(null, targetFile,null,ignoreOnExit);
    }
    @Override
    public int execute(AbstractLogger logger) {
        try {

            log(String.format("try to remove file %s",targetFile), logger,false);
            execute();
            log(String.format("file %s successfully deleted",targetFile), logger,false);
            status=true;
            return 0;
        }
        catch (Exception e){
            log(String.format("error while deleting file %s",targetFile), logger,true);
            log(e.getMessage(), logger,true);
            if(ignoreOnExit){
                status=true;
                return 0;
            }else {
                status = false;
                return -1;
            }
        }
    }

    @Override
    public void execute() {
        File needToDelete=new File(targetFile);
        try {
            if (needToDelete.isDirectory()) {
                FileUtils.deleteDirectory(needToDelete);
            } else {
                Files.deleteIfExists(needToDelete.toPath());
            }
        }catch (IOException e){
            throw new RuntimeException();
        }
    }
}
