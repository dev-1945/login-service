package cl.revent.login.api;

import cl.revent.login.domain.LoginRequest;
import cl.revent.login.service.LoginService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    LoginService loginService;

    @POST
    public Uni<Response> login(LoginRequest request) {
        return loginService.validate(request)
            .onItem().transform(result -> Response.ok(result).build());
    }
}
