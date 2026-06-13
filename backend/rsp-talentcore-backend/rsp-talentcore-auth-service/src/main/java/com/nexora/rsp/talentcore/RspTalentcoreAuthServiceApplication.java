package com.nexora.rsp.talentcore;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class RspTalentcoreAuthServiceApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(RspTalentcoreAuthServiceApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {

		log.info("Auth service startup completed");
	}

	@PreDestroy
	public void onShutdown() {

		log.info("Auth service shutdown initiated");
	}
}
