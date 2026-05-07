package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Person;

public interface GetPersonUseCase {
  Optional<Person> getById(UUID id);
}
