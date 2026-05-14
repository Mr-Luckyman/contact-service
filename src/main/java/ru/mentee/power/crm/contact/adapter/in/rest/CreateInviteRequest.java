package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateInviteRequest {
  @NotBlank(message = "email must not be blank")
  @Email(message = "email must be valid")
  private String email;

  @NotNull(message = "companyId must not be null")
  private UUID companyId;

  @NotBlank(message = "role must not be blank")
  private String role;

  private UUID inviterPersonId;
}
