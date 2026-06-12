package com.gallery.fineart.mfineart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MfineartApplication {

	public static void main(String[] args) {
		SpringApplication.run(MfineartApplication.class, args);
	}

}
