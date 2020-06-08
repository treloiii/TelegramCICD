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

public class VCSCloner {
    private String token;
    private String repositoryName;

    public VCSCloner(String token, String repositoryName) {
        this.token = token;
        this.repositoryName = repositoryName;
    }

    public void cloneRepos(){
        try {
            GitHub gitHub = GitHub.connectUsingOAuth(token);
            val repos = gitHub.getMyself().getAllRepositories();
            GHRepository repository = repos.get(repositoryName);
            Process process = Runtime.getRuntime().exec(new String[]{"git", "clone", String.format("%s.git", repository.getHtmlUrl().toString())});
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
