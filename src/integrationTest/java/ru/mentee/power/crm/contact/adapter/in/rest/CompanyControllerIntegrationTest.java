package ru.mentee.power.crm.contact.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CompanyControllerIntegrationTest extends AbstractControllerIntegrationTest {
  private String personsUrl() {
    return url("/api/v1/persons");
  }

  private String companiesUrl() {
    return url("/api/v1/companies");
  }

  @Test
  void createCompany_WithExistingPerson_Returns201AndNestedPersonRole() {
    String personId = createPerson("Company Contact", "company-contact@example.com");
    Map<String, Object> request =
        Map.of("name", "Acme Inc", "persons", List.of(Map.of("personId", personId, "role", "CEO")));

    ResponseEntity<Map> response = restTemplate.postForEntity(companiesUrl(), request, Map.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getHeaders().getLocation());
    assertEquals("Acme Inc", response.getBody().get("name"));
    List<Map<String, Object>> persons =
        (List<Map<String, Object>>) response.getBody().get("persons");
    assertEquals(1, persons.size());
    assertEquals(personId, persons.getFirst().get("personId"));
    assertEquals("CEO", persons.getFirst().get("role"));
  }

  @Test
  void getCompany_Returns200WithEmptyPersons() {
    String companyId = createCompany("Empty Company");

    ResponseEntity<Map> response =
        restTemplate.getForEntity(companiesUrl() + "/" + companyId, Map.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Empty Company", response.getBody().get("name"));
    assertEquals(List.of(), response.getBody().get("persons"));
  }

  @Test
  void listCompanies_Returns200() {
    createCompany("Listed Company");

    ResponseEntity<List> response =
        restTemplate.getForEntity(companiesUrl() + "?page=0&size=20", List.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertFalse(response.getBody().isEmpty());
  }

  @Test
  void updateCompany_WithValidName_Returns200() {
    String companyId = createCompany("Old Name");
    Map<String, String> request = Map.of("name", "New Name");

    ResponseEntity<Map> response =
        restTemplate.exchange(
            companiesUrl() + "/" + companyId, HttpMethod.PUT, new HttpEntity<>(request), Map.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("New Name", response.getBody().get("name"));
  }

  @Test
  void deleteCompany_RemovesCompanyAndReturns204() {
    String companyId = createCompany("Delete Me");

    ResponseEntity<Void> deleteResponse =
        restTemplate.exchange(
            companiesUrl() + "/" + companyId, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    ResponseEntity<Map> getResponse =
        restTemplate.getForEntity(companiesUrl() + "/" + companyId, Map.class);

    assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
  }

  @Test
  void createCompany_WithUnknownPerson_Returns404() {
    Map<String, Object> request =
        Map.of(
            "name",
            "Broken Company",
            "persons",
            List.of(Map.of("personId", UUID.randomUUID().toString(), "role", "CEO")));

    ResponseEntity<Map> response = restTemplate.postForEntity(companiesUrl(), request, Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("PERSON_NOT_FOUND", response.getBody().get("errorCode"));
  }

  private String createPerson(String fullName, String email) {
    ResponseEntity<Map> response =
        restTemplate.postForEntity(
            personsUrl(), Map.of("fullName", fullName, "email", email), Map.class);
    return (String) response.getBody().get("id");
  }

  private String createCompany(String name) {
    ResponseEntity<Map> response =
        restTemplate.postForEntity(companiesUrl(), Map.of("name", name), Map.class);
    return (String) response.getBody().get("id");
  }
}
