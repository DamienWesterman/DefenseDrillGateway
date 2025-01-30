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

package com.damienwesterman.defensedrill.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DefenseDrillGatewayApplication {
	// TODO: Take a look at each and every endpoint reported in the spring boot dashboard for every microservice
	// TODO: do the cert stuff and activate https (in prod)
	// TODO: Address any startup warnings
	// TODO: Send error handling downstream to mvc somehow?
	// TODO: Finish the security TO-DOs
	// TODO: Check to see what actuator info is available outside of the network
	// TODO: Check other out of network handling and access (does it properly block/allow out/in network? Does the subString method work well, is there always a leading '/'?)

	public static void main(String[] args) {
		SpringApplication.run(DefenseDrillGatewayApplication.class, args);
	}

}
