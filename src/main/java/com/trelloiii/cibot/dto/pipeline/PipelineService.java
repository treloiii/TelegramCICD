package com.trelloiii.cibot.dto.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PipelineService {
    private final PipelineExecutor pipelineExecutor;
    private static PipelineService pipelineService;

    public PipelineService(PipelineExecutor pipelineExecutor) {
        this.pipelineExecutor = pipelineExecutor;
        Thread consumer=new Thread(()->{
            try {
                pipelineExecutor.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        consumer.start();
    }

    public void execute(LoggablePipeline pipeline){
        new Thread(()-> {
            try {
                pipelineExecutor.addPipeline(pipeline);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
