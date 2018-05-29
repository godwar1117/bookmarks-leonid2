package com.bookmarks.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.bookmarks")
@SpringBootApplication
public class BookmarksLeonidApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmarksLeonidApplication.class, args);
	}

}
