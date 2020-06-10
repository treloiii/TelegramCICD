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
    @Column(unique = true)
    private String repositoryName;
    private String oauthToken;
    @Transient
    private String logPath;

    @OneToMany(mappedBy = "pipeline",orphanRemoval = true)
    private List<PipelineHistory> history;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

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
