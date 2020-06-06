package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import lombok.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static com.trelloiii.cibot.dto.logger.LoggerUtils.readLog;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CopyJavaInstruction extends JavaInstruction {
    private String destinationFolder;

    public CopyJavaInstruction(String workingDir, String targetFile, String destinationFolder) {
        super(workingDir, targetFile,null);
        this.destinationFolder = destinationFolder;
    }

    @Override
    public int execute(LogExecutor logExecutor) {
        try{
            readLog(String.format("starting to copy from %s to %s",workingDir,destinationFolder),logExecutor,false);
            execute();
            readLog("copy complete",logExecutor,false);
            status=true;
            return 0;
        }
        catch (Exception e){
            readLog(String.format("error while copy file %s",workingDir),logExecutor,true);
            readLog(e.getMessage(),logExecutor,true);
            status=false;
            return -1;
        }
    }

    @Override
    public void execute() {
        List<String> targetPlusFile=Arrays.asList(targetFile.split("/"));
        targetPlusFile=new LinkedList<>(targetPlusFile);
        targetPlusFile.add(0,workingDir);
        String wildcard=targetPlusFile.get(targetPlusFile.size()-1);
        File fileDir=new File(String.join("/",targetPlusFile.subList(0,targetPlusFile.size()-1))); //dir where search wildcard file
        FileFilter target=new WildcardFileFilter(wildcard); //target is wildcard pattern
        File destinationFolder=new File(this.destinationFolder); //in this dir we copy files
        Optional<File[]> files=Optional.ofNullable(fileDir.listFiles(target));
        files.ifPresent((wildcardFiles)->{
            for(File f:wildcardFiles){
                copy(f,destinationFolder);
            }
        });
    }
    @SneakyThrows
    private void copy(File src,File dist){
        if (src.isDirectory()) {
            FileUtils.copyDirectory(src, dist);
        } else {
            FileUtils.copyFileToDirectory(src,dist);
        }
    }
}
