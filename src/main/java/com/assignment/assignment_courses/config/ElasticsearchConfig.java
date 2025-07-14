package com.assignment.assignment_courses.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.assignment.assignment_courses.Repo")
public class ElasticsearchConfig {
    // No custom configuration needed for Spring Boot 3.x
}
