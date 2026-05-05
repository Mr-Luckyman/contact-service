package ru.mentee.power.crm.contact.usecase.port.in;

import ru.mentee.power.crm.contact.domain.model.Person;

import java.util.Optional;
import java.util.UUID;

public interface GetPersonUseCase {
    Optional<Person> getById(UUID id);
}