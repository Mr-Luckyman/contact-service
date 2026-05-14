package ru.mentee.power.crm.contact.usecase.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.DeletePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetPersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.ListPersonsUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.UpdatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

@RequiredArgsConstructor
public class PersonService
    implements CreatePersonUseCase,
        GetPersonUseCase,
        ListPersonsUseCase,
        UpdatePersonUseCase,
        DeletePersonUseCase {
  private static final int MAX_PAGE_SIZE = 100;

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

  @Override
  public List<Person> list(int page, int size) {
    if (page < 0) {
      throw new IllegalArgumentException("page must be greater than or equal to 0");
    }
    if (size < 1 || size > MAX_PAGE_SIZE) {
      throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
    }
    return personRepository.findAll(page, size);
  }

  @Override
  public Person update(UUID id, String fullName, String email) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (fullName == null || fullName.isBlank()) {
      throw new IllegalArgumentException("fullName must not be blank");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email must not be blank");
    }

    Person existing = personRepository.findById(id).orElseThrow(() -> new PersonNotFound(id));
    personRepository
        .findByEmail(email)
        .filter(person -> !person.getId().equals(id))
        .ifPresent(
            person -> {
              throw new IllegalStateException("Person with email " + email + " already exists");
            });
    return personRepository.save(existing.update(fullName.trim(), email.trim()));
  }

  @Override
  public void delete(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (!personRepository.existsById(id)) {
      throw new PersonNotFound(id);
    }
    personRepository.deleteById(id);
  }

  public static class PersonNotFound extends RuntimeException {
    public PersonNotFound(UUID id) {
      super("Person with id " + id + " not found");
    }
  }
}
