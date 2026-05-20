package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCompanyRequest {
  @NotBlank(message = "name must not be blank")
  private String name;
}
