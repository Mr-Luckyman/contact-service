package ru.mentee.power.crm.contact.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PersonCompanyLink {
  private UUID id;
  private UUID personId;
  private UUID companyId;
  private String role;
  private Instant createdAt;
  private Instant updatedAt;
  private Person person;

  public static PersonCompanyLink create(UUID personId, String role) {
    Instant now = Instant.now();
    return PersonCompanyLink.builder()
        .id(UUID.randomUUID())
        .personId(personId)
        .role(role)
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  public static PersonCompanyLink restore(
      UUID id,
      UUID personId,
      UUID companyId,
      String role,
      Instant createdAt,
      Instant updatedAt,
      Person person) {
    return PersonCompanyLink.builder()
        .id(id)
        .personId(personId)
        .companyId(companyId)
        .role(role)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .person(person)
        .build();
  }
}
