package ru.mentee.power.crm.contact.usecase.port.out;

import ru.mentee.power.crm.contact.domain.model.Person;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository {
    Person save(Person person);
    Optional<Person> findById(UUID id);
    boolean existsByEmail(String email);
}