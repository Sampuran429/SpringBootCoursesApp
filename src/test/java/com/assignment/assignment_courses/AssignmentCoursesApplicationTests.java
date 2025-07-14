package com.assignment.assignment_courses;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AssignmentCoursesApplicationTests {

	@Test
	void contextLoads() {
	}

}
