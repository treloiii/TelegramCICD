package com.trelloiii.cibot.dto.vcs;

import com.google.inject.internal.cglib.core.$LocalVariablesSorter;
import com.trelloiii.cibot.dto.pipeline.PipelineUtils;
import com.trelloiii.cibot.model.Pipeline;
import io.netty.util.Timeout;
import lombok.SneakyThrows;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.TimerTask;

public class VCSWatcher extends TimerTask {
    private final Pipeline pipeline;
    private final Date now;
    @Autowired
    private PipelineUtils pipelineUtils;
    public VCSWatcher(Pipeline pipeline) {
        this.pipeline = pipeline;
        now=new Date();
    }
    @SneakyThrows
    public void watch(){
        GitHub gitHub=GitHub.connectUsingOAuth(pipeline.getOauthToken());
        GHRepository ghRepository= VCSUtils.getRepositoryByFullName(pipeline.getRepositoryName(),gitHub.getMyself().getAllRepositories());
        Date updatedAt=ghRepository.getUpdatedAt();
        if(updatedAt.after(now)){
            System.out.println("UPDATE");
            pipelineUtils.startPipelineQuiet(pipeline);
            now.setTime(System.currentTimeMillis());
        }
    }

    @Override
    public void run() {
        watch();
    }
}
