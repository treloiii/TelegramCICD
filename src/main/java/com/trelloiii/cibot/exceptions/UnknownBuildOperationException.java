package com.trelloiii.cibot.exceptions;

public class UnknownBuildOperationException extends RuntimeException {
    public UnknownBuildOperationException(String operation) {
        super(String.format("Unknown operation %s",operation));
    }
}
