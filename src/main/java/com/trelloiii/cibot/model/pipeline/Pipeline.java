package com.trelloiii.cibot.model.pipeline;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Pipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Transient
    private List<Stage> stages;
    @Transient
    private PipelineConfiguration configuration;
    @OneToMany(mappedBy = "pipeline")
    private List<PipelineHistory> history;
}
