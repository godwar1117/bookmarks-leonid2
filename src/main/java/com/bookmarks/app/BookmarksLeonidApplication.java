package com.bookmarks.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("com.bookmarks")
@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("com.bookmarks.repository")
@EntityScan("com.bookmarks.entity")
public class BookmarksLeonidApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmarksLeonidApplication.class, args);
	}

}
