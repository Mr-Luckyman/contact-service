package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Company;

public interface GetCompanyUseCase {
  Optional<Company> getById(UUID id);
}
