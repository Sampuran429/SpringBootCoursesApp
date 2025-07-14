package com.assignment.assignment_courses.Controller;

import com.assignment.assignment_courses.Model.CourseDocument;
import com.assignment.assignment_courses.Service.CourseService;
import com.assignment.assignment_courses.Service.CourseService.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public Map<String, Object> searchCourses(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "minAge", required = false) Integer minAge,
            @RequestParam(value = "maxAge", required = false) Integer maxAge,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam(value = "sort", required = false, defaultValue = "upcoming") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        if (q != null && q.trim().isEmpty()) {
            q = null;
        }
        SearchResult result = courseService.searchCourses(q, minAge, maxAge, category, type, minPrice, maxPrice, startDate, sort, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("total", result.getTotal());
        response.put("courses", result.getCourses());
        return response;
    }

    @GetMapping("/suggest")
    public java.util.List<String> suggestTitles(
            @RequestParam("q") String q,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return courseService.suggestTitles(q, size);
    }
}
