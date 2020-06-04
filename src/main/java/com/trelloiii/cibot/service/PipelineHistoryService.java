package com.trelloiii.cibot.service;

import com.trelloiii.cibot.dto.pipeline.Instruction;
import com.trelloiii.cibot.dto.pipeline.Stage;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.model.PipelineHistory;
import com.trelloiii.cibot.repository.PipelineHistoryRepository;
import com.trelloiii.cibot.repository.PipelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PipelineHistoryService {
    private final PipelineHistoryRepository pipelineHistoryRepository;
    private final PipelineRepository pipelineRepository;
    public PipelineHistoryService(PipelineHistoryRepository pipelineHistoryRepository, PipelineRepository pipelineRepository) {
        this.pipelineHistoryRepository = pipelineHistoryRepository;
        this.pipelineRepository = pipelineRepository;
    }

    public PipelineHistory writePipelineHistory(Pipeline pipeline, Stage failedStage){
        PipelineHistory history=new PipelineHistory();
        history.setExecutedAt(LocalDateTime.now());
        if(failedStage!=null) {
            history.setFailed_stage(failedStage.getName());
            Instruction failedInstruction = failedStage.getInstructions()
                    .stream()
                    .filter(instruction -> !instruction.getStatus())
                    .findFirst()
                    .get();
            history.setFailed_instruction(failedInstruction.getText());
            history.setStatus(true);
        }
        history.setStatus(false);
        return pipelineHistoryRepository.save(history);
    }
}
