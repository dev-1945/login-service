package cl.revent.login.domain;

import lombok.Data;

@Data
public class LoginResult {
    private Long id;
    private String email;
    private String code;
    private String name;
    private String role;
}
