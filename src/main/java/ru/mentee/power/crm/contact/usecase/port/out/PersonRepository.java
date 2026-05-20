package ru.mentee.power.crm.contact.usecase.port.out;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.power.crm.contact.domain.model.Person;

public interface PersonRepository {
  Person save(Person person);

  Optional<Person> findById(UUID id);

  Optional<Person> findByEmail(String email);

  boolean existsByEmail(String email);

  void deleteById(UUID id);

  Page<Person> findAll(String email, Pageable pageable);
}
