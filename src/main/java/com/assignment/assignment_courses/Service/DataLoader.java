package com.assignment.assignment_courses.Service;

import com.assignment.assignment_courses.Model.CourseDocument;
import com.assignment.assignment_courses.Repo.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataLoader(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (courseRepository.count() == 0) {
            InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();
            List<CourseDocument> courses = objectMapper.readValue(inputStream, new com.fasterxml.jackson.core.type.TypeReference<List<CourseDocument>>() {});
            courseRepository.saveAll(courses);
            System.out.println("Sample courses indexed into Elasticsearch: " + courses.size());
        } else {
            System.out.println("Courses already indexed. Skipping data load.");
        }
    }
}
