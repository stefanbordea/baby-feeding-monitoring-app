package gr.athtech.babyfeedingmonitoringapp.security;

import gr.athtech.babyfeedingmonitoringapp.model.Role;
import gr.athtech.babyfeedingmonitoringapp.service.UserService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

@Provider
public class AuthorizationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";

    @Inject
    private UserService userService;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            return;
        }

        if (method.isAnnotationPresent(DenyAll.class)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Forbidden").build());
            return;
        }

        final MultivaluedMap<String, String> headers = requestContext.getHeaders();

        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        if (authorization == null || authorization.isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build());
            return;
        }

        final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        String usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword.getBytes()));

        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        //Verify the user has access to the resource based on the roles set for the resource
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesSetForTheResource = method.getAnnotation(RolesAllowed.class);
            String[] allowedRoles = rolesSetForTheResource.value();

            if (!isUserAllowed(username, password, allowedRoles)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to access this resource").build());
            }
        } else {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to access this").build());
        }
    }

    private boolean isUserAllowed(final String username, final String password, final String[] rolesSetExpected) {
        Role userRole = userService.checkUserRole(username, password);
        for (String expectedRole : rolesSetExpected) {
            if (Role.valueOf(expectedRole).equals(userRole)) {
                return true;
            }
        }
        return false;
    }
}
