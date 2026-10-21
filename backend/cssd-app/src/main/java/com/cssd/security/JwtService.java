package com.cssd.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expireHours;

    public JwtService(@Value("${cssd.jwt.secret}") String secret,
                      @Value("${cssd.jwt.expire-hours:12}") long expireHours) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            bytes = java.util.Arrays.copyOf(bytes, 32);
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.expireHours = expireHours;
    }

    public String issue(LoginUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getStaffId())
                .claim("no", user.getStaffNo())
                .claim("name", user.getStaffName())
                .claim("role", user.getRole())
                .claim("deptId", user.getDeptId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expireHours, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public LoginUser parse(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return new LoginUser(
                claims.getSubject(),
                claims.get("no", String.class),
                claims.get("name", String.class),
                claims.get("role", String.class),
                claims.get("deptId", String.class)
        );
    }
}
