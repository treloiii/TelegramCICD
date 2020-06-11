package com.trelloiii.cibot.dto.pipeline.instruction;

import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.dto.logger.Logger;

public interface Instruction {
    int execute(AbstractLogger logger);
    void execute();
    default Boolean getStatus(){
        throw new UnsupportedOperationException("getStatus is unsupported");
    }
    default String getText(){
        throw new UnsupportedOperationException("getText is unsupported");
    }
}
