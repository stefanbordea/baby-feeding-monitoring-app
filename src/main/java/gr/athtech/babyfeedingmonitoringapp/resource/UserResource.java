package gr.athtech.babyfeedingmonitoringapp.resource;

import gr.athtech.babyfeedingmonitoringapp.dto.AuthenticationRequest;
import gr.athtech.babyfeedingmonitoringapp.dto.UserDto;
import gr.athtech.babyfeedingmonitoringapp.service.UserService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
public class UserResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/signup")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response signup(AuthenticationRequest authenticationRequest) {
        UserDto userDto = userService.register(authenticationRequest);
        return Response.ok(userDto).build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response login(AuthenticationRequest authenticationRequest) {
        UserDto userDto = userService.login(authenticationRequest);
        return Response.ok(userDto).build();
    }
}
