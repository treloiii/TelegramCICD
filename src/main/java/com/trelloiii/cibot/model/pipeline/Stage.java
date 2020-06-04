package com.trelloiii.cibot.model.pipeline;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.List;

@Data
public class Stage {
    private String name;
    private List<Instruction> instructions;
    private Boolean status;
    private Boolean system=false;
    @SneakyThrows
    public void execute(LogExecutor logExecutor){
        if (!system)
            logExecutor.sendLog(String.format("Starting stage: %s", this.name.toUpperCase()));
        for(Instruction instruction:instructions){
            instruction.execute(logExecutor);
        }
    }
}
