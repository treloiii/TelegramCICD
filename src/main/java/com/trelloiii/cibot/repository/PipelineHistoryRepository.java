package com.trelloiii.cibot.repository;

import com.trelloiii.cibot.model.PipelineHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineHistoryRepository extends JpaRepository<PipelineHistory,Long> {
}
