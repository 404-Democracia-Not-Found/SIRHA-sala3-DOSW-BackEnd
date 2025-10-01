package edu.dosw.sirha.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sirha.security.jwt")
public class JwtProperties {

    private String issuer = "sirha";
    private int expirationMinutes = 60;
    private String secret;
}
