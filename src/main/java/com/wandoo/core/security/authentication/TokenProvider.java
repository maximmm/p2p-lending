package com.wandoo.core.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Optional;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Base64.getEncoder;
import static java.util.Date.from;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Component
public class TokenProvider {

    @Value("${security.jwt.token.secret-key:dummy-key}")
    private String secretKey;

    @Value("${security.jwt.token.expiration:3600}")
    private long tokenExpirationInSeconds;

    @PostConstruct
    public void init() {
        secretKey = getEncoder().encodeToString(secretKey.getBytes());
    }

    public Optional<String> fetchUsername(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return ofNullable(claimsJws.getBody().getSubject());
        } catch (RuntimeException e) {
            return empty();
        }
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(getTokenExpiration())
                .signWith(HS512, secretKey)
                .compact();
    }

    private Date getTokenExpiration() {
        return from(now().plusSeconds(tokenExpirationInSeconds).atZone(systemDefault()).toInstant());
    }

}
