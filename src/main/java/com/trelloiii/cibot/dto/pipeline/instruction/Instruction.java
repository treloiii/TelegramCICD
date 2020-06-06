package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.LogExecutor;

import javax.naming.OperationNotSupportedException;
import javax.ws.rs.NotSupportedException;

public interface Instruction {
    int execute(LogExecutor logExecutor);
    void execute();
    default Boolean getStatus(){
        throw new UnsupportedOperationException("getStatus is unsupported");
    }
    default String getText(){
        throw new UnsupportedOperationException("getText is unsupported");
    }
}
