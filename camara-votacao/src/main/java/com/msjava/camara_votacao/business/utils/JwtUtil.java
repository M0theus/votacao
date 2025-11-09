package com.msjava.camara_votacao.business.utils;

import java.util.Date;

import javax.crypto.SecretKey;

import com.msjava.camara_votacao.business.enums.TipoUsuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 86400000;

    public String generateToken(Integer userId, String username, TipoUsuario tipo, String cpf) {

        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .claim("nome", username)
            .claim("tipo", tipo.name())
            .claim("cpf", cpf)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public TipoUsuario getTipoFromToken(String token) {
        String tipo = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("tipo", String.class);
        return TipoUsuario.valueOf(tipo);
    }
}
