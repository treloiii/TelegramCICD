package com.trelloiii.cibot.dto.vcs;

import com.trelloiii.cibot.dto.pipeline.instruction.NativeUnixInstruction;
import com.trelloiii.cibot.exceptions.GithubAuthException;
import com.trelloiii.cibot.exceptions.GithubRepositoryNotFoundException;
import lombok.val;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.HttpException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.LogOutputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Stream;

public class VCSCloner {
    private final String token;
    private final String repositoryName;

    public VCSCloner(String token, String repositoryName) {
        this.token = token;
        this.repositoryName = repositoryName;
    }

    public void cloneRepos(){
        try {
            removeRepos();
            GitHub gitHub = GitHub.connectUsingOAuth(token);
            Map<String,GHRepository> repos = gitHub.getMyself().getAllRepositories();
            GHRepository repository=VCSUtils.getRepositoryByFullName(repositoryName,repos);//https://<Token>@github.com/user/repo.git
            ProcessResult processResult = new ProcessExecutor("git","clone",String.format("https://%s@github.com/%s.git",token, repository.getFullName()))
                    .redirectError(new LogOutputStream() {
                        @Override
                        protected void processLine(String s) {
                            System.out.println(s);
                        }
                    })
                    .execute();
            System.out.println(processResult.getExitValue());
        }
        catch (HttpException e){
            throw new GithubAuthException();
        }
        catch (NullPointerException e){
            throw new GithubRepositoryNotFoundException();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeRepos() {
        new NativeUnixInstruction(String.format("rm -r %s",repositoryName.split("/")[1]),"./").execute();
    }
}
