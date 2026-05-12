package ru.mentee.power.crm.contact.usecase.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetPersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

@Service
@RequiredArgsConstructor
public class PersonService implements CreatePersonUseCase, GetPersonUseCase {

  private final PersonRepository personRepository;

  @Override
  public Person create(String fullName, String email) {
    // Валидация
    if (fullName == null || fullName.isBlank()) {
      throw new IllegalArgumentException("fullName must not be blank");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email must not be blank");
    }

    // Дедупликация
    if (personRepository.existsByEmail(email)) {
      throw new IllegalStateException("Person with email " + email + " already exists");
    }

    Person person = Person.create(fullName, email);
    return personRepository.save(person);
  }

  @Override
  public Optional<Person> getById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    return personRepository.findById(id);
  }
}
