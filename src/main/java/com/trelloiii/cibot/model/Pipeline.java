package com.trelloiii.cibot.model;
import com.trelloiii.cibot.dto.pipeline.Stage;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
public class Pipeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String repositoryName;
    private String oauthToken;


    @OneToMany(mappedBy = "pipeline")
    private List<PipelineHistory> history;

    @Transient
    private List<Stage> stages;
    @Transient
    private Map<String,Object> configuration;

    public Pipeline(String name, String repositoryName, String oauthToken) {
        this.name=name;
        this.oauthToken=oauthToken;
        this.repositoryName=repositoryName;
    }
}
