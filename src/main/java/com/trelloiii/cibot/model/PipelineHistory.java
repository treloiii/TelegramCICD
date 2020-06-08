package com.trelloiii.cibot.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class PipelineHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;
    private LocalDateTime executedAt;
    private Boolean status;
    private String failed_stage;
    private String failed_instruction;
    private String logPath;
}
