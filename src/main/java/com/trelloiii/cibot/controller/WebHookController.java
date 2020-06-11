package com.trelloiii.cibot.controller;

import com.trelloiii.cibot.dto.vcs.GithubHook;
import com.trelloiii.cibot.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@RestController
public class WebHookController {
    private final PipelineService pipelineService;

    public WebHookController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @PostMapping
    public String works(RequestEntity<LinkedHashMap<String,Object>> githubPayload){
        GithubHook githubHook=GithubHook.from(githubPayload.getBody());
        //TODO запускать тихий конвейер
        return "WORKS";
    }
}
