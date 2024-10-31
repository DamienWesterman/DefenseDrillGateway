package com.damienwesterman.defensedrill.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DefenseDrillGatewayApplication {
	// TODO: Test to make sure that streaming video through this gateway works
	// TODO: FIXME: do a filter or something to only allow connection to certain endpoints from local networks

	public static void main(String[] args) {
		SpringApplication.run(DefenseDrillGatewayApplication.class, args);
	}

}
