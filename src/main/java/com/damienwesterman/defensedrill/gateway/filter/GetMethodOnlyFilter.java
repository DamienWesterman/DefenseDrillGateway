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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Filter to only allow GET method requests.
 */
@Component
@Slf4j
public class GetMethodOnlyFilter
        extends AbstractGatewayFilterFactory<GetMethodOnlyFilter.Config>  {

    public GetMethodOnlyFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (HttpMethod.GET != exchange.getRequest().getMethod()) {
                String clientIpString = "UNKNOWN";
                if (null != exchange.getRequest().getRemoteAddress()) {
                    clientIpString = exchange.getRequest().getRemoteAddress().getAddress().toString();
                }
                String requestedEndpoint = exchange.getRequest().getURI().getPath();
                log.warn("Blocked request from:  clientIp<"
                        + clientIpString + "> | endpoint<" + requestedEndpoint +"> | method<"
                        + exchange.getRequest().getMethod() + ">");
                exchange.getResponse().setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);

                // Do not forward request
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
