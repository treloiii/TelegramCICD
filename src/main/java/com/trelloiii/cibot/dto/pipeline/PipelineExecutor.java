package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.LogExecutor;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineHistoryService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Log
@Component
public class PipelineExecutor {
    private final PipelineHistoryService pipelineHistoryService;
    private final BlockingQueue<LoggablePipeline> pipelineQueue=new ArrayBlockingQueue<>(3);

    public PipelineExecutor(PipelineHistoryService pipelineHistoryService) {
        this.pipelineHistoryService = pipelineHistoryService;
    }

    public void addPipeline(LoggablePipeline pipeline) throws InterruptedException {
        pipelineQueue.put(pipeline);
    }

    public void execute() throws InterruptedException {
        while (true) {
            LoggablePipeline loggablePipeline = pipelineQueue.take();
            Pipeline pipeline=loggablePipeline.getPipeline();
            LogExecutor logExecutor=new LogExecutor(loggablePipeline.getSendMessageConsumer(),loggablePipeline.getId());
            List<Stage> stageList = pipeline.getStages();
            Stage failed=null;
            for (Stage stage : stageList) {
                int code=stage.execute(logExecutor);
                if(code!=0) {
                    failed=stage;
                    break;
                }
            }
            pipelineHistoryService.writePipelineHistory(pipeline,failed);
        }
    }
}
