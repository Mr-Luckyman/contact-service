package ru.mentee.power.crm.contact.adapter.in.rest;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class PersonResponse {
  private UUID id;
  private String fullName;
  private String email;
  private String phone;
  private Instant createdAt;
  private Instant updatedAt;
}
