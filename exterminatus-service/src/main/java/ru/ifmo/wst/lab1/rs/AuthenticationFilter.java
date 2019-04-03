package ru.ifmo.wst.lab1.rs;

import lombok.extern.slf4j.Slf4j;
import ru.ifmo.wst.lab.ExterminatusPaths;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

@Slf4j
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
    @Inject
    private AuthChecker authChecker;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (authChecker == null) {
            return;
        }
        Method method = resourceInfo.getResourceMethod();

        if (!method.isAnnotationPresent(Secured.class)) {
            return;
        }

        //Get request headers
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();

        //Fetch authorization header
        final List<String> authorization = headers.get(ExterminatusPaths.AUTHORIZATION_PROPERTY);

        //If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("You cannot access this resource").build());
            return;
        }

        //Get encoded username and password
        final String encodedUserPassword = authorization.get(0).replaceFirst(ExterminatusPaths.AUTHENTICATION_SCHEME + " ", "");

        //Decode username and password
        String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes()));

        //Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username;
        final String password;
        try {
            username = tokenizer.nextToken();
            password = tokenizer.nextToken();
        } catch (NoSuchElementException exc) {
            log.debug("No username or password provided {}", usernameAndPassword);
            requestContext.abortWith(Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("You cannot access this resource")
                    .build()
            );
            return;
        }

        if (!isUserAllowed(username, password)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("You cannot access this resource").build());
            return;
        }
    }

    private boolean isUserAllowed(final String username, final String password) {
        return authChecker != null && authChecker.check(username, password);
    }

}
