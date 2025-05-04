package com.mission_entreprise.web_api.exceptions;

public class PullNotFound extends RuntimeException {
    public PullNotFound(String message) {
        super(message);
    }
}
