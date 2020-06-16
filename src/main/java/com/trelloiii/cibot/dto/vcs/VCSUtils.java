package com.trelloiii.cibot.dto.vcs;

import org.kohsuke.github.GHRepository;

import java.util.Map;

public class VCSUtils {
    public static GHRepository getRepositoryByFullName(String name, Map<String,GHRepository> repos){
        return repos.values()
                .stream()
                .filter(grp->grp.getFullName().equals(name))
                .findAny()
                .orElseThrow(NullPointerException::new);
    }
}
