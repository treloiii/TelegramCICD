package com.trelloiii.cibot.exceptions;

public class GithubAuthException extends RuntimeException {
    public GithubAuthException() {
        super("Authentication required. Check validity of auth token");
    }

    public GithubAuthException(String message) {
        super(message);
    }
}
