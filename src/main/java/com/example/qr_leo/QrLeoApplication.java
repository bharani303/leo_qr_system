package com.example.qr_leo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class QrLeoApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrLeoApplication.class, args);
	}

}
