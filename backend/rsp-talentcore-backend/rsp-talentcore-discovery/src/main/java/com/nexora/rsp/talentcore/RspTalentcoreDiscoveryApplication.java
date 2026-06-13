package com.nexora.rsp.talentcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RspTalentcoreDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(RspTalentcoreDiscoveryApplication.class, args);
	}

}
