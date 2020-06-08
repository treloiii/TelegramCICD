package com.trelloiii.cibot.service;

import com.trelloiii.cibot.dto.pipeline.instruction.Instruction;
import com.trelloiii.cibot.dto.pipeline.Stage;
import com.trelloiii.cibot.exceptions.PipelineNotFoundException;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.model.PipelineHistory;
import com.trelloiii.cibot.repository.PipelineHistoryRepository;
import com.trelloiii.cibot.repository.PipelineRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        history.setPipeline(pipeline);
        history.setStatus(false);
        history.setLogPath(pipeline.getLogPath());
        if(failedStage!=null) {
            history.setFailed_stage(failedStage.getName());
            Instruction failedNativeUnixInstruction = failedStage.getInstructions()
                    .stream()
                    .filter(instruction -> !instruction.getStatus())
                    .findFirst()
                    .get();
            history.setFailed_instruction(failedNativeUnixInstruction.getText());
            history.setStatus(true);
        }
        return pipelineHistoryRepository.save(history);
    }
    public List<PipelineHistory> getHistoryByPipelineId(String pipelineId){
        return pipelineHistoryRepository.findTop3ByPipelineOrderByIdDesc(
                pipelineRepository.findById(Long.valueOf(pipelineId))
                .orElseThrow(PipelineNotFoundException::new)
        );
    }
    public List<PipelineHistory> getHistoryByPipeline(Pipeline pipeline){
        return pipelineHistoryRepository.findTop3ByPipelineOrderByIdDesc(pipeline);
    }
}
