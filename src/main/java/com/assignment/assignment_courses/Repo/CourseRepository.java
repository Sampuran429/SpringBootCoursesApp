package com.assignment.assignment_courses.Repo;

import com.assignment.assignment_courses.Model.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
    // Custom query methods (if needed) can be added here
}
