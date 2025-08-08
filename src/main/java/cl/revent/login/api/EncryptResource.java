package cl.revent.login.api;

import cl.revent.login.service.LoginService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/encrypt")
@Produces(MediaType.TEXT_PLAIN)
public class EncryptResource {

    @Inject
    LoginService loginService;

    @GET
    @Path("/{text}")
    public Response encrypt(@PathParam("text") String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Texto vacío").build();
        }

        String hash = loginService.encrypt(plainText);
        return Response.ok(hash).build();
    }
}
