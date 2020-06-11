package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.dto.logger.AbstractLogger;
import com.trelloiii.cibot.dto.logger.QuietLogger;
import com.trelloiii.cibot.model.Pipeline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuietPipeline implements ExecutablePipeline {
    private Pipeline pipeline;
    private AbstractLogger logger;

    @Override
    public void initLogger() {
        logger=new QuietLogger(pipeline);
    }
}
