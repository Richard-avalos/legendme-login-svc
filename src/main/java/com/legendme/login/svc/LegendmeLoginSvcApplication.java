package com.legendme.login.svc;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LegendmeLoginSvcApplication {
	public static void main(String[] args) {
//		Dotenv dotenv = Dotenv.load();
//		System.setProperty("CLIENT_ID", dotenv.get("CLIENT_ID"));
//		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		SpringApplication.run(LegendmeLoginSvcApplication.class, args);
	}

}
