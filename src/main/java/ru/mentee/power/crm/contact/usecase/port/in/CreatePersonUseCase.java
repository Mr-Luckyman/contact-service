package ru.mentee.power.crm.contact.usecase.port.in;

import ru.mentee.power.crm.contact.domain.model.Person;

public interface CreatePersonUseCase {
  Person create(String fullName, String email, String phone);
}
