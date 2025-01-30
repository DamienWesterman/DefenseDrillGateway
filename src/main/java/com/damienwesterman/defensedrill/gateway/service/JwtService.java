/****************************\
 *      ________________      *
 *     /  _             \     *
 *     \   \ |\   _  \  /     *
 *      \  / | \ / \  \/      *
 *      /  \ | / | /  /\      *
 *     /  _/ |/  \__ /  \     *
 *     \________________/     *
 *                            *
 \****************************/
/*
 * Copyright 2025 Damien Westerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.damienwesterman.defensedrill.gateway.service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.damienwesterman.defensedrill.gateway.util.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for JWT interaction. Provides methods to generate, check, and interpret JWT.
 */
@Service
@Slf4j
public class JwtService {
    private static final String CLAIMS_KEY_ROLES = "roles";

    /**
     * TODO: doc comments
     * @param serverHttpRequest
     * @return
     */
    @NonNull
    public String extractToken(@NonNull ServerHttpRequest serverHttpRequest) {
        // Check if using bearer token
        String cookieString = serverHttpRequest.getHeaders().getFirst(HttpHeaders.COOKIE);
        if (null == cookieString || !cookieString.startsWith("jwt=")) {
            // No authorization
            return "";
        }

        return cookieString.substring(4); // "jwt=".size() = 4
    }

    /**
     * Check if a JWT is valid.
     *
     * @param jwt String JWT
     * @return true/false if the token is valid
     */
    public boolean isTokenValid(@NonNull String jwt) {
        return Optional.ofNullable(getClaims(jwt)).isPresent();
    }

    /**
     * Extract a user's username from a JWT.
     *
     * @param jwt String JWT
     * @return Username
     */
    @NonNull
    public String extractUsername(@NonNull String jwt) {
        return Optional.ofNullable(getClaims(jwt))
            .map(Claims::getSubject)
            .orElse("");
    }

    /**
     * Extract a user's roles from a JWT.
     *
     * @param jwt String JWT
     * @return List of roles
     */
    @NonNull
    public List<String> extractRoles(@NonNull String jwt) {
        String rolesString = Optional.ofNullable(getClaims(jwt))
            .map(claims -> (String) claims.get(CLAIMS_KEY_ROLES))
            .orElse("");

        return Arrays.asList(rolesString.split(",")).stream()
            .filter(prefacedRole -> !prefacedRole.isBlank())
            .map(prefacedRole -> prefacedRole.substring(5)) // "ROLE_".size() = 5
            .collect(Collectors.toList());
    }

    /**
     * Extract the claims from a JWT string. May return null on error.
     *
     * @param jwt String JWT
     * @return Claims object, may be null on error
     */
    @Nullable
    private Claims getClaims(@NonNull String jwt) {
        if (jwt.isBlank()) {
            return null;
        }

        try {
            return Jwts.parser()
                .verifyWith(generatePublicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        } catch (JwtException e) {
            log.warn(e.getMessage());
        }

        return null;
    }

    private PublicKey generatePublicKey() {
        byte[] decodedKey = Base64.getDecoder().decode(Constants.PUBLIC_KEY);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Error during public key generation", e);
            throw new RuntimeException(e);
        }
    }
}
