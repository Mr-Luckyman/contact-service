package ru.mentee.power.crm.contact.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class   PersonControllerIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  private String baseUrl() {
    return "http://localhost:" + port + "/api/v1/persons";
  }

  @Test
  void createPerson_ShouldReturn201AndLocation() {
    Map<String, String> request =
        Map.of("fullName", "Иван Петров", "email", "ivan@example.com", "phone", "+79091234567");

    var response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    assertNotNull(response.getBody());
    assertEquals("Иван Петров", response.getBody().get("fullName"));
    assertEquals("ivan@example.com", response.getBody().get("email"));
    assertEquals("+79091234567", response.getBody().get("phone"));
    assertNotNull(response.getBody().get("id"));
  }

  @Test
  void createPerson_DuplicateEmail_ShouldReturn409() {
    Map<String, String> request = Map.of("fullName", "Дубликат", "email", "duplicate@example.com");
    restTemplate.postForEntity(baseUrl(), request, Map.class);

    var response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    var body = response.getBody();
    assertNotNull(body);
    assertEquals("PERSON_EMAIL_CONFLICT", body.get("errorCode"));
  }

  @Test
  void createPerson_InvalidEmail_ShouldReturn400() {
    Map<String, String> request = Map.of("fullName", "Иван", "email", "not-an-email");

    var response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void createPerson_EmptyFullName_ShouldReturn400() {
    Map<String, String> request = Map.of("fullName", "", "email", "test@example.com");

    var response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getPerson_ShouldReturn200() {
    Map<String, String> request = Map.of("fullName", "Петр Сидоров", "email", "petr@example.com");
    var createResponse = restTemplate.postForEntity(baseUrl(), request, Map.class);
    assertNotNull(createResponse.getBody());
    String id = (String) createResponse.getBody().get("id");

    var getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);

    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertNotNull(getResponse.getBody());
    assertEquals("Петр Сидоров", getResponse.getBody().get("fullName"));
    assertEquals("petr@example.com", getResponse.getBody().get("email"));
  }

  @Test
  void getPerson_NotFound_ShouldReturn404() {
    String nonExistentId = UUID.randomUUID().toString();

    var response = restTemplate.getForEntity(baseUrl() + "/" + nonExistentId, Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    var body = response.getBody();
    assertNotNull(body);
    assertEquals("PERSON_NOT_FOUND", body.get("errorCode"));
  }

  @Test
  void updatePerson_ShouldReturn200() {
    Map<String, String> createRequest =
        Map.of("fullName", "Иван Петров", "email", "ivan.update@example.com");
    var createResponse = restTemplate.postForEntity(baseUrl(), createRequest, Map.class);
    assertNotNull(createResponse.getBody());
    String id = (String) createResponse.getBody().get("id");

    Map<String, String> updateRequest =
        Map.of(
            "fullName",
            "Иван Сидоров",
            "email",
            "ivan.update@example.com",
            "phone",
            "+79091111111");

    restTemplate.put(baseUrl() + "/" + id, updateRequest);

    var getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);
    assertNotNull(getResponse.getBody());
    assertEquals("Иван Сидоров", getResponse.getBody().get("fullName"));
    assertEquals("ivan.update@example.com", getResponse.getBody().get("email"));
    assertEquals("+79091111111", getResponse.getBody().get("phone"));
  }

  @Test
  void updatePerson_WithNewEmail_ShouldReturn200() {
    Map<String, String> createRequest =
        Map.of("fullName", "Иван Петров", "email", "old@example.com");
    var createResponse = restTemplate.postForEntity(baseUrl(), createRequest, Map.class);
    assertNotNull(createResponse.getBody());
    String id = (String) createResponse.getBody().get("id");

    Map<String, String> updateRequest =
        Map.of("fullName", "Иван Петров", "email", "new@example.com");

    restTemplate.put(baseUrl() + "/" + id, updateRequest);

    var getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);
    assertNotNull(getResponse.getBody());
    assertEquals("new@example.com", getResponse.getBody().get("email"));
  }

  @Test
  void updatePerson_WithEmailConflict_ShouldReturn409() {
    Map<String, String> request1 = Map.of("fullName", "Первый", "email", "first@example.com");
    restTemplate.postForEntity(baseUrl(), request1, Map.class);

    Map<String, String> request2 = Map.of("fullName", "Второй", "email", "second@example.com");
    var createResponse = restTemplate.postForEntity(baseUrl(), request2, Map.class);
    assertNotNull(createResponse.getBody());
    String id = (String) createResponse.getBody().get("id");

    Map<String, String> updateRequest = Map.of("fullName", "Второй", "email", "first@example.com");

    var updateResponse =
        restTemplate.exchange(
            org.springframework.http.RequestEntity.put(java.net.URI.create(baseUrl() + "/" + id))
                .body(updateRequest),
            Map.class);

    assertEquals(HttpStatus.CONFLICT, updateResponse.getStatusCode());
    var body = updateResponse.getBody();
    assertNotNull(body);
    assertEquals("PERSON_EMAIL_CONFLICT", body.get("errorCode"));
  }

  @Test
  void updatePerson_NotFound_ShouldReturn404() {
    String nonExistentId = UUID.randomUUID().toString();
    Map<String, String> updateRequest = Map.of("fullName", "Иван", "email", "any@example.com");

    var response =
        restTemplate.exchange(
            org.springframework.http.RequestEntity.put(
                    java.net.URI.create(baseUrl() + "/" + nonExistentId))
                .body(updateRequest),
            Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void deletePerson_ShouldReturn204() {
    Map<String, String> createRequest =
        Map.of("fullName", "Для удаления", "email", "delete@example.com");
    var createResponse = restTemplate.postForEntity(baseUrl(), createRequest, Map.class);
    assertNotNull(createResponse.getBody());
    String id = (String) createResponse.getBody().get("id");

    ResponseEntity<Void> deleteResponse =
        restTemplate.exchange(
            org.springframework.http.RequestEntity.delete(java.net.URI.create(baseUrl() + "/" + id))
                .build(),
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

    var getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  @Test
  void deletePerson_NotFound_ShouldReturn404() {
    String nonExistentId = UUID.randomUUID().toString();

    var response =
        restTemplate.exchange(
            org.springframework.http.RequestEntity.delete(
                    java.net.URI.create(baseUrl() + "/" + nonExistentId))
                .build(),
            Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void listPersons_ShouldReturnPage() {
    Map<String, String> request1 = Map.of("fullName", "Иван Петров", "email", "ivan@example.com");
    Map<String, String> request2 = Map.of("fullName", "Петр Сидоров", "email", "petr@example.com");
    restTemplate.postForEntity(baseUrl(), request1, Map.class);
    restTemplate.postForEntity(baseUrl(), request2, Map.class);

    ResponseEntity<PersonPageResponse> response =
        restTemplate.getForEntity(baseUrl() + "?page=0&size=10", PersonPageResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().getPage());
    assertEquals(10, response.getBody().getSize());
    assertTrue(response.getBody().getTotal() >= 2);
    assertTrue(response.getBody().getItems().size() >= 2);
  }

  @Test
  void listPersons_WithEmailFilter_ShouldReturnFiltered() {
    Map<String, String> request1 = Map.of("fullName", "Иван Петров", "email", "ivan@example.com");
    Map<String, String> request2 = Map.of("fullName", "Петр Сидоров", "email", "petr@example.com");
    restTemplate.postForEntity(baseUrl(), request1, Map.class);
    restTemplate.postForEntity(baseUrl(), request2, Map.class);

    ResponseEntity<PersonPageResponse> response =
        restTemplate.getForEntity(
            baseUrl() + "?email=ivan@example.com&page=0&size=10", PersonPageResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getTotal());
    assertEquals("Иван Петров", response.getBody().getItems().getFirst().getFullName());
  }

  @Test
  void listPersons_WithSizeExceedingMax_ShouldReturn400() {
    ResponseEntity<ProblemDetail> response =
        restTemplate.getForEntity(baseUrl() + "?size=101", ProblemDetail.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void listPersons_WithNegativePage_ShouldReturn400() {
    ResponseEntity<ProblemDetail> response =
        restTemplate.getForEntity(baseUrl() + "?page=-1", ProblemDetail.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }
}
