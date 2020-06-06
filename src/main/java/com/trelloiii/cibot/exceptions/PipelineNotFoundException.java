package com.trelloiii.cibot.exceptions;

public class PipelineNotFoundException extends RuntimeException {
    public PipelineNotFoundException() {
        super("Pipeline not found");
    }

    public PipelineNotFoundException(String message) {
        super(message);
    }
}
