package com.xperia.xpense_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {
		"com.xperia.xpense_tracker",
		"org.xperia.repository",
		"org.xperia.service"
})
@EnableAsync
@EnableCaching
public class XpenseTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(XpenseTrackerApplication.class, args);
	}

}
