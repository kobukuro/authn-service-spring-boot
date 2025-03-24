package com.peter.authnservicespringboot.util;

import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    @Value("${secret-key}")
    private String secret;

    @Value("${jwt.verification.expiration}")
    private long verificationTokenExpiration;

    public String generateVerificationToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + verificationTokenExpiration))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret));
    }
}
