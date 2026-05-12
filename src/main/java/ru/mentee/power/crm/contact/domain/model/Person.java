package ru.mentee.power.crm.contact.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Person {
  private final UUID id;
  private final String fullName;
  private final String email;
  private final Instant createdAt;
  private Instant updatedAt;

  public static Person create(String fullName, String email) {
    UUID nowId = UUID.randomUUID();
    Instant now = Instant.now();
    return new Person(nowId, fullName, email, now, now);
  }

  public Person update(String newFullName, String newEmail) {
    return new Person(this.id, newFullName, newEmail, this.createdAt, Instant.now());
  }
}