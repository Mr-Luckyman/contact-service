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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PersonControllerIntegrationTest {

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
    return "http://localhost:" + port + "/api/v1/people";
  }

  @Test
  void createPerson_ShouldReturn201AndLocation() {
    // Given
    Map<String, String> request =
        Map.of(
            "fullName", "Иван Петров",
            "email", "ivan@example.com");

    // When
    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    assertNotNull(response.getBody());
    assertEquals("Иван Петров", response.getBody().get("fullName"));
    assertEquals("ivan@example.com", response.getBody().get("email"));
    assertNotNull(response.getBody().get("id"));
  }

  @Test
  void getPerson_ShouldReturn200() {
    // Given
    Map<String, String> request =
        Map.of(
            "fullName", "Петр Сидоров",
            "email", "petr@example.com");
    ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), request, Map.class);
    String id = (String) createResponse.getBody().get("id");

    // When
    ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);

    // Then
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertEquals("Петр Сидоров", getResponse.getBody().get("fullName"));
    assertEquals("petr@example.com", getResponse.getBody().get("email"));
  }

  @Test
  void createPerson_DuplicateEmail_ShouldReturn409() {
    // Given
    Map<String, String> request =
        Map.of(
            "fullName", "Дубликат",
            "email", "duplicate@example.com");
    restTemplate.postForEntity(baseUrl(), request, Map.class);

    // When
    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    // Then
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertEquals("PERSON_EMAIL_CONFLICT", body.get("errorCode"));
  }

  @Test
  void createPerson_InvalidEmail_ShouldReturn400() {
    // Given
    Map<String, String> request =
        Map.of(
            "fullName", "Иван",
            "email", "not-an-email");

    // When
    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void createPerson_EmptyFullName_ShouldReturn400() {
    // Given
    Map<String, String> request =
        Map.of(
            "fullName", "",
            "email", "test@example.com");

    // When
    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    // Then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertNotNull(response.getBody());
  }

  @Test
  void getPerson_NotFound_ShouldReturn404() {
    // Given
    String nonExistentId = UUID.randomUUID().toString();

    // When
    ResponseEntity<Map> response =
        restTemplate.getForEntity(baseUrl() + "/" + nonExistentId, Map.class);

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Map<String, Object> body = response.getBody();
    assertEquals("PERSON_NOT_FOUND", body.get("errorCode"));
  }
}
