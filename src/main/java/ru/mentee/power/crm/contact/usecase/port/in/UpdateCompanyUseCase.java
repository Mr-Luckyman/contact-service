package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Company;

public interface UpdateCompanyUseCase {
  Company updateName(UUID id, String name);
}
