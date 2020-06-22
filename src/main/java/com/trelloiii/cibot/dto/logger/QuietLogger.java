package com.trelloiii.cibot.dto.logger;

import com.trelloiii.cibot.model.Pipeline;

public class QuietLogger extends AbstractLogger {
    public QuietLogger(Pipeline pipeline) {
        super(pipeline);
    }
    @Override
    public void sendLog(String log){}

    @Override
    public void sendForceLog(String log) {}

    @Override
    public void sendLogFile() {}
}
