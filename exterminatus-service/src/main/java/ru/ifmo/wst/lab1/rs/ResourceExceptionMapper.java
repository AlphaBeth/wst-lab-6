package ru.ifmo.wst.lab1.rs;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ResourceExceptionMapper implements ExceptionMapper<ResourceException> {
    @Override
    public Response toResponse(ResourceException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getReason()).build();
    }
}
