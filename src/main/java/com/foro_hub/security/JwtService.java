package com.foro_hub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secretKey;

    @Getter
    @Value("${api.security.token.expiration-ms}")
    private Long expirationMs;

    public String generateToken(final UserDetails userDetails) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + expirationMs);

        log.info("[ForoHub/JWT] - Generando token para usuario: {}", userDetails.getUsername());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        final boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

        if (isValid) {
            log.debug("[ForoHub/JWT] - Token válido para usuario: {}", username);
        } else {
            log.warn("[ForoHub/JWT] - Token inválido o expirado para usuario: {}", username);
        }

        return isValid;
    }

    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        // return Jwts.SIG.HS256.key();
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
