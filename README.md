# Assignment Courses – Spring Boot + Elasticsearch

## Overview
This application is a Spring Boot REST API for searching and filtering a set of sample course documents indexed in Elasticsearch. It demonstrates full-text search, filtering, sorting, pagination, autocomplete suggestions, and fuzzy matching for course titles.

- **Backend:** Spring Boot, Spring Data Elasticsearch
- **Search Engine:** Elasticsearch (Dockerized)
- **Sample Data:** 50+ course objects in `sample-courses.json`

---

## Features
- **Bulk Indexing:** Loads sample courses into Elasticsearch on startup
- **Search Endpoint:** Full-text, filter, sort, and paginate courses
- **Autocomplete:** Suggests course titles as you type
- **Fuzzy Search:** Tolerates typos in course title search (bonus)
- **Integration Tests:** End-to-end tests using Testcontainers for Elasticsearch

---

## Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose

---

## 1. Running Elasticsearch with Docker

The application expects Elasticsearch to be running locally on `localhost:9200` **without authentication**.

### Start Elasticsearch
```sh
docker-compose up -d
```

This uses the provided `docker-compose.yml` file:
```yaml
version: '3.7'
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.1
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    ports:
      - 9200:9200
```

### Verify Elasticsearch is Running
```sh
curl http://localhost:9200
```
You should see a JSON response with cluster info.

---

## 2. Running the Spring Boot Application

### Build and Run
```sh
mvn clean package
java -jar target/assignment_courses-0.0.1-SNAPSHOT.jar
```
Or, for development:
```sh
mvn spring-boot:run
```

### What Happens on Startup?
- The app reads `src/main/resources/sample-courses.json`.
- All courses are bulk indexed into the `courses` index in Elasticsearch (if not already present).

---

## 3. API Endpoints

### 3.1. Search Courses
**Endpoint:** `GET /api/search`

#### Query Parameters
- `q` – (optional) Search keyword (title/description, full-text)
- `minAge`, `maxAge` – (optional) Age range filter
- `category` – (optional) Exact match filter
- `type` – (optional) Exact match filter (ONE_TIME, COURSE, CLUB)
- `minPrice`, `maxPrice` – (optional) Price range filter
- `startDate` – (optional) ISO-8601 date, only courses on/after this date
- `sort` – (optional) `upcoming` (default), `priceAsc`, `priceDesc`
- `page` – (optional) Page number (default: 0)
- `size` – (optional) Page size (default: 10)

#### Example Request
```sh
curl "http://localhost:8080/api/search?q=math&category=Math&minAge=7&sort=priceAsc&page=0&size=5"
```

#### Example Response
```json
{
  "total": 12,
  "courses": [
    {
      "id": "course-1",
      "title": "Math for Beginners",
      "description": "...",
      "category": "Math",
      "type": "COURSE",
      "gradeRange": "1st–3rd",
      "minAge": 7,
      "maxAge": 9,
      "price": 49.99,
      "nextSessionDate": "2025-06-10T15:00:00Z",
      "suggest": ["Math for Beginners"]
    },
    // ...more courses
  ]
}
```

---

### 3.2. Autocomplete Suggestions
**Endpoint:** `GET /api/search/suggest`

#### Query Parameters
- `q` – (required) Partial course title
- `size` – (optional) Max number of suggestions (default: 10)

#### Example Request
```sh
curl "http://localhost:8080/api/search/suggest?q=phy"
```

#### Example Response
```json
[
  "Physics for Kids",
  "Physical Education Club"
]
```

---

## 4. Project Structure
```
assignment_courses/
├── docker-compose.yml
├── README.md
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/assignment/assignment_courses/
│   │   │   ├── AssignmentCoursesApplication.java
│   │   │   ├── config/ElasticsearchConfig.java
│   │   │   ├── Model/CourseDocument.java
│   │   │   ├── Repo/CourseRepository.java
│   │   │   ├── Service/CourseService.java
│   │   │   ├── Service/DataLoader.java
│   │   │   └── Controller/CourseController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── sample-courses.json
│   └── test/
│       └── java/com/assignment/assignment_courses/
│           └── CourseSearchIntegrationTest.java
```

---

## 5. Configuration

### `src/main/resources/application.properties`
```
spring.application.name=assignment_courses
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=5s
spring.elasticsearch.socket-timeout=3s
logging.level.org.springframework.data.elasticsearch.core=DEBUG
```

---

## 6. Notes
- The application will not re-index courses if they already exist in Elasticsearch.
- For fuzzy search (typo tolerance), the main search endpoint can be enhanced to use fuzziness in the query.
- For integration tests, see `src/test/java/com/assignment/assignment_courses/CourseSearchIntegrationTest.java`.

---

## 7. Troubleshooting
- **Elasticsearch not running:** Ensure Docker is running and port 9200 is available.
- **Data not indexed:** Check application logs for errors on startup.
- **API not reachable:** Ensure the app is running on port 8080 (default).

---

## 8. Integration Testing with Testcontainers

This project includes integration tests that use [Testcontainers](https://www.testcontainers.org/) to spin up a real Elasticsearch instance for end-to-end testing.

- **Test class:** `src/test/java/com/assignment/assignment_courses/CourseSearchIntegrationTest.java`
- **What it tests:**
  - Indexing and searching courses by category and full-text
  - Autocomplete suggestions
- **How to run:**

  1. Ensure Docker is running on your machine.
  2. Run:
     ```sh
     mvn test
     ```
  3. The tests will automatically start and stop a temporary Elasticsearch container.

---

