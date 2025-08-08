package cl.revent.login.domain;

import lombok.Data;

@Data
public class LoginRequest {
    private String usr;
    private String pwd;
}
