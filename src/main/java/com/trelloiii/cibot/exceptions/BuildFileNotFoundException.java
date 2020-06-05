package com.trelloiii.cibot.exceptions;

public class BuildFileNotFoundException extends RuntimeException {
    public BuildFileNotFoundException() {
        super("Build file not found in project root directory");
    }

    public BuildFileNotFoundException(String message) {
        super(message);
    }
}
