package com.trelloiii.cibot.controller;

import com.trelloiii.cibot.dto.logger.QuietLogger;
import com.trelloiii.cibot.dto.pipeline.CallBackUtils;
import com.trelloiii.cibot.dto.pipeline.QuietPipeline;
import com.trelloiii.cibot.dto.vcs.GithubHook;
import com.trelloiii.cibot.model.Pipeline;
import com.trelloiii.cibot.service.PipelineService;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Optional;

@RestController
public class WebHookController {
    private final CallBackUtils callBackUtils;

    public WebHookController(CallBackUtils callBackUtils) {
        this.callBackUtils = callBackUtils;
    }

    @PostMapping
    public String works(RequestEntity<LinkedHashMap<String,Object>> githubPayload){
        GithubHook githubHook=GithubHook.from(
                Optional.ofNullable(githubPayload.getBody())
                .orElseThrow(RuntimeException::new)
        );
        callBackUtils.startPipelineQuiet(githubHook);
        //TODO запускать тихий конвейер
        return "WORKS";
    }
}
