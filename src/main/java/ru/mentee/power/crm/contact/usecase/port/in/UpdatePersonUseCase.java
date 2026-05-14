package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Person;

public interface UpdatePersonUseCase {
  Person update(UUID id, String fullName, String email);
}
