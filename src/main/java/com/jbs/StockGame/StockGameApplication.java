package com.jbs.StockGame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockGameApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockGameApplication.class, args);
	}
}
