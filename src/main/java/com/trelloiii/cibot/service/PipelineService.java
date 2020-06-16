package com.trelloiii.cibot.service;

import com.trelloiii.cibot.dto.pipeline.ExecutablePipeline;
import com.trelloiii.cibot.dto.pipeline.LoggablePipeline;
import com.trelloiii.cibot.dto.pipeline.PipelineExecutor;
import com.trelloiii.cibot.dto.vcs.VCSWatcher;
import com.trelloiii.cibot.exceptions.PipelineNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.repository.PipelineRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PipelineService {
    private final PipelineExecutor pipelineExecutor;
    private final PipelineRepository pipelineRepository;
    private final Timer checkTimer = new Timer();
    private VCSWatcher lastWatcher;
    private final BeanFactory beanFactory;

    public PipelineService(PipelineExecutor pipelineExecutor, PipelineRepository pipelineRepository, BeanFactory beanFactory) {
        this.pipelineExecutor = pipelineExecutor;
        this.pipelineRepository = pipelineRepository;
        Thread consumer = new Thread(() -> {
            try {
                pipelineExecutor.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        consumer.start();
        this.beanFactory = beanFactory;
    }

    public void execute(ExecutablePipeline pipeline) {
        new Thread(() -> {
            try {
                pipelineExecutor.addPipeline(pipeline);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Pipeline savePipeline(Pipeline pipeline) {
        if (pipeline.getTimer() != null) {
            if(lastWatcher!=null) {
                lastWatcher.cancel();
                checkTimer.purge();
            }
            if(pipeline.getTimer()<0){ //delete timer
                pipeline.setTimer(null);
            }else {
                lastWatcher = beanFactory.getBean(VCSWatcher.class, pipeline);
                checkTimer.scheduleAtFixedRate(lastWatcher, 0, pipeline.getTimer());
            }
        }
        return pipelineRepository.save(pipeline);
    }

    public void removePipeline(Pipeline pipeline) {
        pipelineRepository.delete(pipeline);
    }

    public void removePipeline(Long id) {
        pipelineRepository.deleteById(id);
    }

    public List<Pipeline> getPipelines() {
        return pipelineRepository.findAll();
    }

    public Pipeline getPipeline(String data) {
        return pipelineRepository.findById(Long.valueOf(data))
                .orElseThrow(() -> new PipelineNotFoundException("pipeline not found"));
    }

    public Pipeline getPipelineByReposName(String reposName) {
        return pipelineRepository.findByRepositoryName(reposName);
    }
}
