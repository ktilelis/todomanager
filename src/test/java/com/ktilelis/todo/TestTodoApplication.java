package com.ktilelis.todo;

import org.springframework.boot.SpringApplication;

public class TestTodoApplication {

	public static void main(String[] args) {
		SpringApplication.from(TodoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
