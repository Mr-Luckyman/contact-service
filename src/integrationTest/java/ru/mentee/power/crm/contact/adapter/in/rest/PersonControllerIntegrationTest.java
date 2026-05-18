package ru.mentee.power.crm.contact.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PersonControllerIntegrationTest extends AbstractControllerIntegrationTest {
  private String baseUrl() {
    return url("/api/v1/persons");
  }

  @Test
  void createPerson_ShouldReturn201AndLocation() {
    Map<String, String> request = Map.of("fullName", "Иван Петров", "email", "ivan@example.com");

    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    assertNotNull(response.getBody());
    assertEquals("Иван Петров", response.getBody().get("fullName"));
    assertEquals("ivan@example.com", response.getBody().get("email"));
    assertNotNull(response.getBody().get("id"));
  }

  @Test
  void getPerson_ShouldReturn200() {
    Map<String, String> request = Map.of("fullName", "Петр Сидоров", "email", "petr@example.com");
    ResponseEntity<Map> createResponse = restTemplate.postForEntity(baseUrl(), request, Map.class);
    String id = (String) createResponse.getBody().get("id");

    ResponseEntity<Map> getResponse = restTemplate.getForEntity(baseUrl() + "/" + id, Map.class);

    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    assertEquals("Петр Сидоров", getResponse.getBody().get("fullName"));
    assertEquals("petr@example.com", getResponse.getBody().get("email"));
  }

  @Test
  void createPerson_DuplicateEmail_ShouldReturn409() {
    Map<String, String> request = Map.of("fullName", "Дубликат", "email", "duplicate@example.com");
    restTemplate.postForEntity(baseUrl(), request, Map.class);

    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("PERSON_EMAIL_CONFLICT", response.getBody().get("errorCode"));
  }

  @Test
  void createPerson_InvalidEmail_ShouldReturn400() {
    Map<String, String> request = Map.of("fullName", "Иван", "email", "not-an-email");

    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void createPerson_EmptyFullName_ShouldReturn400() {
    Map<String, String> request = Map.of("fullName", "", "email", "test@example.com");

    ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), request, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  void getPerson_NotFound_ShouldReturn404() {
    ResponseEntity<Map> response =
        restTemplate.getForEntity(baseUrl() + "/" + UUID.randomUUID(), Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("PERSON_NOT_FOUND", response.getBody().get("errorCode"));
  }
}
