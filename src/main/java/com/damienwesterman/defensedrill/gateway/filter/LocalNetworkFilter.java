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

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO: Doc comments
 */
@Component
@Slf4j
public class LocalNetworkFilter
        extends AbstractGatewayFilterFactory<LocalNetworkFilter.Config> {

    public LocalNetworkFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            boolean isLocalNetworkRequest = false;
            // TODO: FIXME: START HERE:
            /*
             * User https://en.wikipedia.org/wiki/Reserved_IP_addresses and sort by Scope, and use all Private Network addresses
             *      This will go well with solutions from https://stackoverflow.com/questions/577363/how-to-check-if-an-ip-address-is-from-a-particular-network-netmask-in-java
             * Also make sure to check things like exchange.getRequest().getRemoteAddress().getAddress().isLinkLocalAddress() AND exchange.getRequest().getRemoteAddress().getAddress().isLoopbackAddress()
             * Check the latency on these things, don't want it to be too long, though it shouldn't
             */
            if (null != clientIp) {
                if (clientIp.startsWith("192.168.")
                        || clientIp.startsWith("10.")
                        || clientIp.equals("127.0.0.1")) {
                    isLocalNetworkRequest = true;
                }
            }
            if (isLocalNetworkRequest) { // TODO: Change this to be !isLocalNetworkRequest
                String requestedEndpoint = exchange.getRequest().getURI().getPath();
                log.warn("Blocked request from:  clientIp<"
                        + clientIp + "> | endpoint<" + requestedEndpoint +">");
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
