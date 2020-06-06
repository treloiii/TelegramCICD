package com.trelloiii.cibot.dto.pipeline.instruction;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class JavaInstruction implements Instruction {
    String workingDir;
    String targetFile;
    Boolean status;

    //copy
    //delete
    //create
    //read
    //write
}
