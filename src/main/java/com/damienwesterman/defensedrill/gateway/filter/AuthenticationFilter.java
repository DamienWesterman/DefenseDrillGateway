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

package com.damienwesterman.defensedrill.gateway.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.damienwesterman.defensedrill.gateway.service.JwtService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter to check if the user is authenticated via JWT.
 */
@Component
@Slf4j
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final JwtService jwtService;

    public AuthenticationFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("permittedRoles");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String jwt = jwtService.extractToken(exchange.getRequest());

            if (!isAuthorized(jwt, config.getPermittedRoles())) {
                logUnauthorizedRequest(exchange.getRequest());

                boolean isApiRequest = exchange.getRequest().getHeaders()
                        .getOrDefault(HttpHeaders.CONTENT_TYPE, List.of())
                        .contains(MediaType.APPLICATION_JSON_VALUE);
                if (isApiRequest) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                } else {
                    exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                    String errorMessage = "";
                    if (jwt.isBlank()) {
                        errorMessage = "Please%20Log%20In";
                    } else {
                        errorMessage = "Not Authorized";
                    }
                    exchange.getResponse().getHeaders().set(
                        HttpHeaders.LOCATION,
                        "/login?error=" + errorMessage);
                }

                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    private boolean isAuthorized(String jwt, List<String> permittedRoles) {
        if (!jwtService.isTokenValid(jwt)) {
            return false;
        }

        List<String> userRoles = jwtService.extractRoles(jwt);
        for (String userRole : userRoles) {
            if (permittedRoles.contains(userRole)) {
                return true;
            }
        }

        return false;
    }

    private void logUnauthorizedRequest(ServerHttpRequest request) {
        String jwt = jwtService.extractToken(request);
        String requestedEndpoint = request.getURI().getPath();
        String clientIpString = "UNKNOWN";

        if (null != request.getRemoteAddress()) {
            clientIpString = request.getRemoteAddress().getAddress().toString();
        }

        log.warn("Blocked request for endpoint: " + requestedEndpoint);
        log.warn("From client ip: " + clientIpString);
        log.warn("Using jwt: " + jwt);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class Config {
        private String permittedRoles;

        public List<String> getPermittedRoles() {
            return Arrays.asList(permittedRoles.split("\\|"));
        }
    }
}
