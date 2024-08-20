package com.realworld.springmongo.security;

import com.realworld.springmongo.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {
    public String extractToken(String authorizationHeader) {
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new InvalidRequestException("Authorization Header", "has no `Bearer` prefix");
        }
        var tokenStarts = "Bearer ".length();
        return authorizationHeader.substring(tokenStarts);
    }
}
