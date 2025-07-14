package com.assignment.assignment_courses;

import org.springframework.boot.SpringApplication;

public class TestAssignmentCoursesApplication {

	public static void main(String[] args) {
		SpringApplication.from(AssignmentCoursesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
