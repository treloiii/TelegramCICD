package com.trelloiii.cibot.controller;

import com.trelloiii.cibot.dto.pipeline.PipelineUtils;
import com.trelloiii.cibot.dto.vcs.GithubHook;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Optional;

@RestController
public class WebHookController {
    private final PipelineUtils pipelineUtils;

    public WebHookController(PipelineUtils pipelineUtils) {
        this.pipelineUtils = pipelineUtils;
    }

    @PostMapping
    public String works(RequestEntity<LinkedHashMap<String,Object>> githubPayload){
        GithubHook githubHook=GithubHook.from(
                Optional.ofNullable(githubPayload.getBody())
                .orElseThrow(RuntimeException::new)
        );
        pipelineUtils.startPipelineQuiet(githubHook);
        //TODO запускать тихий конвейер
        return "WORKS";
    }
}
