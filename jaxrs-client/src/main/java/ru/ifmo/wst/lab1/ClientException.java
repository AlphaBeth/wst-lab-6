package ru.ifmo.wst.lab1;


import lombok.Getter;

import javax.ws.rs.core.Response;

public class ClientException extends RuntimeException {
    @Getter
    private final Response.StatusType status;

    public ClientException(String s, Response.StatusType status) {
        super(s);
        this.status = status;
    }
}
