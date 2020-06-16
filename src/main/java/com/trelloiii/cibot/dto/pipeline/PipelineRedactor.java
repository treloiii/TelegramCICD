package com.trelloiii.cibot.dto.pipeline;

import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PipelineRedactor {
    public static List<String> names= Arrays.asList("name","repository name","token","vcs branch","timer");
    private PipelineService pipelineService;
    private Boolean redact=false;
    private String pipelineId;
    private String field;

    @Autowired
    public PipelineRedactor(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }
    public void clear(){
        field=null;
        pipelineId=null;
        redact=false;
    }
    public boolean checkField(){
        return field!=null;
    }

    public void redact(String value) {
        Pipeline pipeline=pipelineService.getPipeline(pipelineId);
        switch (field){
            case "name":
                pipeline.setName(value);
                break;
            case "repository name":
                pipeline.setRepositoryName(value);
                break;
            case "token":
                pipeline.setOauthToken(value);
                break;
            case "vcs branch":
                pipeline.setBranch(value);
            case "timer":
                long timer=Long.parseLong(value);
                timer=timer*60*1000;
                pipeline.setTimer(timer);
        }
        pipelineService.savePipeline(pipeline);
    }
}
