package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.model.Pipeline;

public interface ExecutablePipeline {
    AbstractLogger getLogger();
    Pipeline getPipeline();
    void initLogger();
}
