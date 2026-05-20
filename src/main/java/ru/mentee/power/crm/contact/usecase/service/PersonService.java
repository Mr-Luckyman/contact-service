package ru.mentee.power.crm.contact.usecase.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contact.adapter.in.rest.PersonNotFoundException;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.DeletePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetPersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.ListPersonsUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.UpdatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService
    implements CreatePersonUseCase,
        GetPersonUseCase,
        UpdatePersonUseCase,
        DeletePersonUseCase,
        ListPersonsUseCase {

  private final PersonRepository personRepository;

  @Override
  public Person create(String fullName, String email, String phone) {
    if (personRepository.existsByEmail(email)) {
      throw new IllegalStateException("Person with email " + email + " already exists");
    }

    Person person = Person.create(fullName, email, phone);
    return personRepository.save(person);
  }

  @Override
  public Optional<Person> getById(UUID id) {
    return personRepository.findById(id);
  }

  @Override
  public Page<Person> list(Query query) {
    if (query.size() > 100) {
      throw new IllegalArgumentException("size must not exceed 100");
    }
    if (query.page() < 0) {
      throw new IllegalArgumentException("page must be >= 0");
    }

    Pageable pageable = PageRequest.of(query.page(), query.size());
    return personRepository.findAll(query.email(), pageable);
  }

  @Override
  public Person update(UUID id, UpdatePersonCommand command) {
    Person person =
        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));

    // Если email изменился - проверить уникальность
    if (!person.getEmail().equals(command.email())) {
      if (personRepository.existsByEmail(command.email())) {
        throw new IllegalStateException("Person with email " + command.email() + " already exists");
      }
    }

    Person updated = person.update(command.fullName(), command.email(), command.phone());
    return personRepository.save(updated);
  }

  @Override
  public void delete(UUID id) {
    if (personRepository.findById(id).isEmpty()) {
      throw new PersonNotFoundException(id);
    }

    personRepository.deleteById(id);
  }
}
