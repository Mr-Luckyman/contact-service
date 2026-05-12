package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePersonRequest {
  @NotBlank(message = "fullName must not be blank")
  private String fullName;

  @NotBlank(message = "email must not be blank")
  @Email(message = "email must be valid")
  private String email;
}
