package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.dto.logger.Logger;
import org.apache.commons.io.FileUtils;
import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RemoveJavaInstruction extends JavaInstruction {
    public RemoveJavaInstruction(String targetFile) {
        super(null, targetFile,null);
    }
    @Override
    public int execute(AbstractLogger logger) {
        try {
            readLog(String.format("try to remove file %s",targetFile), logger,false);
            execute();
            readLog(String.format("file %s successfully deleted",targetFile), logger,false);
            status=true;
            return 0;
        }
        catch (Exception e){
            readLog(String.format("error while deleting file %s",targetFile), logger,true);
            readLog(e.getMessage(), logger,true);
            status=false;
            return -1;
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
