package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.UUID;

public interface DeleteCompanyUseCase {
  void delete(UUID id);
}
