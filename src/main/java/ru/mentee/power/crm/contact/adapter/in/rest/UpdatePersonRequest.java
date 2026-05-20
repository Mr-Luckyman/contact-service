package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePersonRequest {
  @NotBlank(message = "fullName must not be blank")
  private String fullName;

  @NotBlank(message = "email must not be blank")
  @Email(message = "email must be valid")
  private String email;

  @Size(max = 50, message = "phone length must not exceed 50")
  private String phone;
}
