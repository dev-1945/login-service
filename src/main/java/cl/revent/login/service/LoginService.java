package cl.revent.login.service;

import cl.revent.login.domain.LoginRequest;
import cl.revent.login.domain.LoginResult;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.Tuple;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import at.favre.lib.crypto.bcrypt.BCrypt;

@ApplicationScoped
public class LoginService {

    @Inject
    Pool pool;

    @Inject
    Logger log;

    public Uni<LoginResult> validate(LoginRequest request) {
        String sql = "SELECT * FROM read_login($1::jsonb)";

        JsonObject json = new JsonObject()
        .put("usr", request.getUsr())
        .put("pwd", request.getPwd());
    
        // Log de la consulta y parámetros
        log.infov("Ejecutando SQL: {0} con parámetros: {1}", sql, json);
        log.infov("Recibido desde front: usr={0}, pwd={1}", request.getUsr(), request.getPwd());
        log.infov("JSON generado: {0}", json);
                LoginResult emptyResult = new LoginResult();
        emptyResult.setId(0L);
        emptyResult.setCode("");
        emptyResult.setEmail("");
        emptyResult.setName("");
        emptyResult.setRole("guest");

        return pool
            .preparedQuery(sql)
            .execute(Tuple.of(json))
            .onItem().transform(rows -> {
                if (!rows.iterator().hasNext()) {
                    log.warn("Login fallido: usuario no encontrado o contraseña incorrecta.");
                    return emptyResult;
                }

                log.warn("Login encontrado.");
                Row row = rows.iterator().next();

                try {
                    LoginResult result = new LoginResult();
                    result.setId(row.getLong("id"));
                    result.setCode(row.getString("code"));
                    result.setEmail(row.getString("email"));
                    result.setName(row.getString("name"));
                    result.setRole(row.getString("role"));

                    log.infov("Login exitoso para usuario: {0} (ID: {1})", result.getCode(), result.getId());
                    return result;

                } catch (Exception e) {
                    log.error("Error al construir LoginResult", e);
                    return emptyResult;
                }
            })
            .onFailure().invoke(err -> {
                log.error("Error durante validación de login", err);
            })
            .onFailure().recoverWithItem(() -> emptyResult);
    }

    public String encrypt(String plainText) {
        return BCrypt.withDefaults().hashToString(12, plainText.toCharArray());
    }
}
