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

import java.net.InetAddress;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Filter to check if a request is coming from inside a private network, otherwise request is refused.
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
            boolean isLocalNetworkRequest = false;
            String clientIpString = "UNKNOWN";

            if (null != exchange.getRequest().getRemoteAddress()) {
                InetAddress clientIp = exchange.getRequest().getRemoteAddress().getAddress();
                clientIpString = clientIp.toString();
                // Strip the leading '/', don't know why it's there
                clientIpString = clientIpString.substring(1);

                if (null != clientIp) {
                    if (clientIpString.startsWith("192.168.")
                            || clientIpString.startsWith("10.")
                            || clientIpString.matches("172.1[6-9].*")
                            || clientIpString.startsWith("172.2")
                            || clientIpString.matches("172.3[01].*")
                            || clientIp.isLinkLocalAddress()
                            || clientIp.isLoopbackAddress()) {
                        isLocalNetworkRequest = true;
                    }
                }
            }

            if (!isLocalNetworkRequest) {
                String requestedEndpoint = exchange.getRequest().getURI().getPath();
                log.warn("Blocked request from:  clientIp<"
                        + clientIpString + "> | endpoint<" + requestedEndpoint +">");
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
