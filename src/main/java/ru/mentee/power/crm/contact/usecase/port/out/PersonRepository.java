package ru.mentee.power.crm.contact.usecase.port.out;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Person;

public interface PersonRepository {
  Person save(Person person);

  Optional<Person> findById(UUID id);

  boolean existsByEmail(String email);
}
