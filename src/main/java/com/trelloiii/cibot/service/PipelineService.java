package com.trelloiii.cibot.service;

import com.trelloiii.cibot.dto.pipeline.LoggablePipeline;
import com.trelloiii.cibot.dto.pipeline.PipelineExecutor;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.repository.PipelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PipelineService {
    private final PipelineExecutor pipelineExecutor;
    private static PipelineService pipelineService;
    private final PipelineRepository pipelineRepository;
    public PipelineService(PipelineExecutor pipelineExecutor, PipelineRepository pipelineRepository) {
        this.pipelineExecutor = pipelineExecutor;
        this.pipelineRepository = pipelineRepository;
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

    public Pipeline savePipeline(Pipeline pipeline){
        return pipelineRepository.save(pipeline);
    }

    public List<Pipeline> getPipelines() {
        return pipelineRepository.findAll();
    }

    public Pipeline getPipeline(String data) {
        return pipelineRepository.findById(Long.valueOf(data))
                .orElseThrow(()->new RuntimeException("pipeline not found"));
    }
}
