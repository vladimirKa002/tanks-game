package com.vladimirKa002.Tanks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.util.Random;

@SpringBootApplication
public class TanksApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TanksApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(TanksApplication.class, args);
	}

	public static String getId() {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
		int targetStringLength = 20;
		Random rnd = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			buffer.append(characters.charAt(rnd.nextInt(characters.length())));
		}
		return buffer.toString();
	}
}
