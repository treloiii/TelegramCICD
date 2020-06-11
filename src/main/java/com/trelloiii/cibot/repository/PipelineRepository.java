package com.trelloiii.cibot.repository;

import com.trelloiii.cibot.model.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineRepository extends JpaRepository<Pipeline,Long> {
    void deleteById(Long id);
    Pipeline findByRepositoryName(String name);
}
