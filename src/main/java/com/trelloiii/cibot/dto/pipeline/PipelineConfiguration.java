package com.trelloiii.cibot.dto.pipeline;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PipelineConfiguration {
    private boolean deleteAfter=true;

    public PipelineConfiguration(boolean deleteAfter) {
        this.deleteAfter = deleteAfter;
    }
}
