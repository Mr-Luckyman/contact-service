package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.List;
import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Company;

public interface CreateCompanyUseCase {
  Company create(String name, List<PersonLinkCommand> personLinks);

  record PersonLinkCommand(UUID personId, String role) {}
}
