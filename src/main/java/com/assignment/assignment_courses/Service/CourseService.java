package com.assignment.assignment_courses.Service;

import com.assignment.assignment_courses.Model.CourseDocument;
import com.assignment.assignment_courses.Repo.CourseRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final CourseRepository courseRepository;

    public SearchResult searchCourses(
            String q,
            Integer minAge,
            Integer maxAge,
            String category,
            String type,
            Double minPrice,
            Double maxPrice,
            ZonedDateTime startDate,
            String sort,
            int page,
            int size
    ) {
        Criteria criteria = new Criteria();

        // Full-text search on title (fuzzy) and description (exact)
        if (q != null && !q.isEmpty()) {
            Criteria titleCriteria = new Criteria("title").fuzzy(q);
            Criteria descCriteria = new Criteria("description").matches(q);
            criteria = criteria.or(titleCriteria).or(descCriteria);
        }

        // Range filters
        if (minAge != null) {
            criteria = criteria.and(new Criteria("minAge").greaterThanEqual(minAge));
        }
        if (maxAge != null) {
            criteria = criteria.and(new Criteria("maxAge").lessThanEqual(maxAge));
        }
        if (minPrice != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(minPrice));
        }
        if (maxPrice != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(maxPrice));
        }

        // Exact filters
        if (category != null && !category.isEmpty()) {
            criteria = criteria.and(new Criteria("category").is(category));
        }
        if (type != null && !type.isEmpty()) {
            criteria = criteria.and(new Criteria("type").is(type));
        }

        // Date filter
        if (startDate != null) {
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate));
        }

        // Sorting
        Sort sortObj;
        if ("priceAsc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.ASC, "price");
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "price");
        } else {
            sortObj = Sort.by(Sort.Direction.ASC, "nextSessionDate");
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);
        List<CourseDocument> courses = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new SearchResult(hits.getTotalHits(), courses);
    }

    public List<String> suggestTitles(String partialTitle, int size) {
        Criteria criteria = new Criteria("title").startsWith(partialTitle);
        Pageable pageable = PageRequest.of(0, size);
        CriteriaQuery query = new CriteriaQuery(criteria, pageable);
        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);
        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getTitle())
                .distinct()
                .collect(Collectors.toList());
    }

    @Data
    public static class SearchResult {
        private long total;
        private List<CourseDocument> courses;
        public SearchResult(long total, List<CourseDocument> courses) {
            this.total = total;
            this.courses = courses;
        }
    }
}
