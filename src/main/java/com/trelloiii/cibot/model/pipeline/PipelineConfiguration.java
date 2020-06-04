package com.trelloiii.cibot.model.pipeline;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PipelineConfiguration {
    private String name;
    private String dist;
    private String moveTo;
}
