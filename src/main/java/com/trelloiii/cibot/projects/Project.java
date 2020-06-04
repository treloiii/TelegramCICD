package com.trelloiii.cibot.projects;

import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.vcs.Repository;
import lombok.Data;

@Data
public class Project {
    private String name;
    private Repository repository;
    private Pipeline pipeline;
}
