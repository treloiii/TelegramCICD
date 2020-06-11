package com.trelloiii.cibot.dto.vcs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kohsuke.github.GHRepository;

import java.util.LinkedHashMap;

@Data
@AllArgsConstructor
public class GithubHook {
    private String repository;
    private String branch;

    public static GithubHook from(LinkedHashMap<String, Object> body) {
        LinkedHashMap<String, Object> repos = (LinkedHashMap<String, Object>) body.get("repository");
        String rawBranch=(String)body.get("ref");
        String parsedBranch=rawBranch.split("/")[2];
        return new GithubHook((String)repos.get("full_name"), parsedBranch);
    }
}
