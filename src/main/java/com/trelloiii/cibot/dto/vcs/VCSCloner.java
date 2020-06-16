package com.trelloiii.cibot.dto.vcs;

import com.trelloiii.cibot.dto.pipeline.instruction.NativeUnixInstruction;
import com.trelloiii.cibot.exceptions.GithubAuthException;
import com.trelloiii.cibot.exceptions.GithubRepositoryNotFoundException;
import lombok.val;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.HttpException;

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
            GitHub gitHub = GitHub.connectUsingOAuth(token);
            Map<String,GHRepository> repos = gitHub.getMyself().getAllRepositories();
            GHRepository repository=VCSUtils.getRepositoryByFullName(repositoryName,repos);//https://<Token>@github.com/user/repo.git
            Process process = Runtime.getRuntime().exec(new String[]{"git", "clone", String.format("https://%s@github.com/%s.git",token, repository.getFullName())});
            int res = process.waitFor();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println(res);
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
        new NativeUnixInstruction(String.format("rm -r %s",repositoryName),"./").execute();
    }
}
