package com.trelloiii.cibot.dto.pipeline;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PipelineConfiguration {
    private String name;
    private String dist;
    private String moveTo;
    private String target;
}
