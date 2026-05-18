package ru.mentee.power.crm.contact.usecase.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Person;

public interface PersonRepository {
  Person save(Person person);

  Optional<Person> findById(UUID id);

  Optional<Person> findByEmail(String email);

  List<Person> findAll(int page, int size);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  boolean existsByEmail(String email);
}
