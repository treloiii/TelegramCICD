package com.trelloiii.cibot.repository;

import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.model.PipelineHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface PipelineHistoryRepository extends JpaRepository<PipelineHistory,Long> {
    List<PipelineHistory> findFirst3ByPipelineOrderByIdDesc(Pipeline pipeline);
}
