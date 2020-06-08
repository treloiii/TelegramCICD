package com.trelloiii.cibot.exceptions;

public class GithubRepositoryNotFoundException extends RuntimeException{
    public GithubRepositoryNotFoundException() {
        super("Github repository with given name not found");
    }

    public GithubRepositoryNotFoundException(String message) {
        super(message);
    }
}
