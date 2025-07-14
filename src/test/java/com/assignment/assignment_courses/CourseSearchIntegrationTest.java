package com.assignment.assignment_courses;

import com.assignment.assignment_courses.Model.CourseDocument;
import com.assignment.assignment_courses.Repo.CourseRepository;
import com.assignment.assignment_courses.Service.CourseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class CourseSearchIntegrationTest {
    @Container
    public static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.1")
                    .withEnv("discovery.type", "single-node")
                    .withEnv("xpack.security.enabled", "false");

    @DynamicPropertySource
    static void setElasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        
        CourseDocument course1 = new CourseDocument(
                "test-1", "Physics 101", "Intro to Physics", "Science", "COURSE", "9th–10th",
                14, 16, 99.99, ZonedDateTime.parse("2025-06-10T15:00:00Z")
        );
        CourseDocument course2 = new CourseDocument(
                "test-2", "Math Club", "Fun with Math", "Math", "CLUB", "6th–8th",
                11, 13, 59.99, ZonedDateTime.parse("2025-07-01T10:00:00Z")
        );
        courseRepository.saveAll(Arrays.asList(course1, course2));
    }

    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
    }

    @Test
    void testSearchByCategory() {
        var result = courseService.searchCourses(null, null, null, "Math", null, null, null, null, null, 0, 10);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getCourses().get(0).getTitle()).isEqualTo("Math Club");
    }

    @Test
    void testFullTextSearch() {
        var result = courseService.searchCourses("Physics", null, null, null, null, null, null, null, null, 0, 10);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getCourses().get(0).getTitle()).isEqualTo("Physics 101");
    }

    @Test
    void testAutocompleteSuggest() {
        List<String> suggestions = courseService.suggestTitles("Phy", 5);
        assertThat(suggestions).contains("Physics 101");
    }
} 