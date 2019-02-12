package ru.ifmo.wst.lab1.rs;

import lombok.Getter;

import javax.ws.rs.core.Response;

public class ResourceException extends RuntimeException {
    @Getter
    private final String reason;

    private final Response.Status status;

    public ResourceException(String reason) {
        this(reason, Response.Status.BAD_REQUEST);
    }

    public ResourceException(String reason, Response.Status status) {
        this(null, reason, status);
    }

    public ResourceException(String message, String reason, Response.Status status) {
        super(reason);
        this.reason = reason;
        this.status = status;
    }
}
