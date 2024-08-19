package com.realworld.springmongo.security;

import com.realworld.springmongo.user.UserTokenProvider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.realworld.springmongo.exceptions.InvalidRequestException;

import java.security.KeyPair;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtSigner implements UserTokenProvider {

    private final KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
    private final JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(keyPair.getPublic())
            .build();
    private final JwtProperties jwtProperties;

    public Jws<Claims> validate(String jwt) {
        try {
            return jwtParser.parseClaimsJws(jwt);
        } catch (JwtException | ClassCastException e) {
            throw new InvalidRequestException("Token", "is invalid");
        }
    }

    public String generateToken(String userId) {
        return Jwts.builder()
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .setSubject(userId)
                .setExpiration(expirationDate())
                .compact();
    }
    public boolean isTokenExpired(String token) {
        return validate(token).getBody().getExpiration().before(new Date());
    }

    private Date expirationDate() {
        var expirationDate = System.currentTimeMillis() + getSessionTime();
        return new Date(expirationDate);
    }

    private long getSessionTime() {
        return jwtProperties.getSessionTime() * 1000L;
    }

    @Override
    public String getToken(String userId) {
        return generateToken(userId);
    }
}
