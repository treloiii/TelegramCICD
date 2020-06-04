package com.trelloiii.cibot.dto;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import com.trelloiii.cibot.model.pipeline.Instruction;
import com.trelloiii.cibot.model.pipeline.Pipeline;
import com.trelloiii.cibot.model.pipeline.Stage;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Log
public class PipelineExecutor {
    private final BlockingQueue<LoggablePipeline> pipelineQueue=new ArrayBlockingQueue<>(3);
    public void addPipeline(LoggablePipeline pipeline) throws InterruptedException {
        pipelineQueue.put(pipeline);
    }

    public void execute() throws InterruptedException {
        while (true) {
            LoggablePipeline loggablePipeline = pipelineQueue.take();
            Pipeline pipeline=loggablePipeline.getPipeline();
            LogExecutor logExecutor=new LogExecutor(loggablePipeline.getSendMessageConsumer(),loggablePipeline.getId());
            List<Stage> stageList = pipeline.getStages();
            for (Stage stage : stageList) {
                stage.execute(logExecutor);
            }
        }
    }
}
