package ru.mentee.power.crm.contact.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.mentee.power.crm.contact.adapter.out.persistence.InviteJpaEntity;
import ru.mentee.power.crm.contact.adapter.out.persistence.InviteJpaRepository;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;

class InviteControllerIntegrationTest extends AbstractControllerIntegrationTest {
  @Autowired private InviteJpaRepository inviteJpaRepository;

  private String personsUrl() {
    return url("/api/v1/persons");
  }

  private String companiesUrl() {
    return url("/api/v1/companies");
  }

  private String invitesUrl() {
    return url("/api/v1/invites");
  }

  @Test
  void createInvite_Returns201WithPendingStatusAndEightCharCode() {
    String companyId = createCompany("Invite Company");
    Map<String, Object> request =
        Map.of("email", "invitee@example.com", "companyId", companyId, "role", "Sales Manager");

    ResponseEntity<Map> response = restTemplate.postForEntity(invitesUrl(), request, Map.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("PENDING", response.getBody().get("status"));
    assertEquals(8, ((String) response.getBody().get("referralCode")).length());
    assertNotNull(response.getBody().get("expiresAt"));
  }

  @Test
  void createInvite_WithDuplicateActiveInvite_Returns409() {
    String companyId = createCompany("Duplicate Invite Company");
    Map<String, Object> request =
        Map.of("email", "duplicate-invite@example.com", "companyId", companyId, "role", "CEO");
    restTemplate.postForEntity(invitesUrl(), request, Map.class);

    ResponseEntity<Map> response = restTemplate.postForEntity(invitesUrl(), request, Map.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("INVITE_CONFLICT", response.getBody().get("errorCode"));
  }

  @Test
  void getInvite_WithExpiredPendingInvite_MarksExpiredAndReturns410() {
    String companyId = createCompany("Expired Invite Company");
    String code = "EXPIRD01";
    InviteJpaEntity expired =
        new InviteJpaEntity(
            UUID.randomUUID(),
            "expired@example.com",
            UUID.fromString(companyId),
            "CEO",
            null,
            code,
            InviteStatus.PENDING,
            Instant.now().minusSeconds(60),
            Instant.now().minusSeconds(120),
            Instant.now().minusSeconds(120));
    inviteJpaRepository.save(expired);

    ResponseEntity<Map> response = restTemplate.getForEntity(invitesUrl() + "/" + code, Map.class);

    assertEquals(HttpStatus.GONE, response.getStatusCode());
    assertEquals("INVITE_EXPIRED", response.getBody().get("errorCode"));
    assertEquals(
        InviteStatus.EXPIRED, inviteJpaRepository.findByReferralCode(code).get().getStatus());
  }

  @Test
  void acceptInvite_WithExistingPerson_DeduplicatesPersonAndAddsCompanyLink() {
    String personId = createPerson("Existing Invitee", "existing-invitee@example.com");
    String companyId = createCompany("Accept Invite Company");
    String referralCode = createInvite("existing-invitee@example.com", companyId, "Advisor");
    Map<String, String> acceptRequest =
        Map.of("email", "existing-invitee@example.com", "fullName", "Existing Invitee");

    ResponseEntity<Map> acceptResponse =
        restTemplate.postForEntity(
            invitesUrl() + "/" + referralCode + "/accept", acceptRequest, Map.class);
    ResponseEntity<Map> companyResponse =
        restTemplate.getForEntity(companiesUrl() + "/" + companyId, Map.class);

    assertEquals(HttpStatus.OK, acceptResponse.getStatusCode());
    assertEquals("ACCEPTED", acceptResponse.getBody().get("status"));
    List<Map<String, Object>> persons =
        (List<Map<String, Object>>) companyResponse.getBody().get("persons");
    assertEquals(1, persons.size());
    assertEquals(personId, persons.getFirst().get("personId"));
    assertEquals("Advisor", persons.getFirst().get("role"));
  }

  @Test
  void acceptInvite_WithMismatchedEmail_Returns400() {
    String companyId = createCompany("Mismatch Invite Company");
    String referralCode = createInvite("right@example.com", companyId, "CEO");
    Map<String, String> acceptRequest =
        Map.of("email", "wrong@example.com", "fullName", "Wrong Person");

    ResponseEntity<Map> response =
        restTemplate.postForEntity(
            invitesUrl() + "/" + referralCode + "/accept", acceptRequest, Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("VALIDATION_FAILED", response.getBody().get("errorCode"));
  }

  @Test
  void acceptInvite_RepeatedAccept_Returns409() {
    String companyId = createCompany("Repeat Invite Company");
    String referralCode = createInvite("repeat@example.com", companyId, "CEO");
    Map<String, String> acceptRequest =
        Map.of("email", "repeat@example.com", "fullName", "Repeat Person");
    restTemplate.postForEntity(
        invitesUrl() + "/" + referralCode + "/accept", acceptRequest, Map.class);

    ResponseEntity<Map> response =
        restTemplate.postForEntity(
            invitesUrl() + "/" + referralCode + "/accept", acceptRequest, Map.class);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("INVITE_CONFLICT", response.getBody().get("errorCode"));
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

  private String createInvite(String email, String companyId, String role) {
    ResponseEntity<Map> response =
        restTemplate.postForEntity(
            invitesUrl(), Map.of("email", email, "companyId", companyId, "role", role), Map.class);
    return (String) response.getBody().get("referralCode");
  }
}
