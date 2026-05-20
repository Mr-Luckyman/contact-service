package ru.mentee.power.crm.contact.usecase.port.in;

import org.springframework.data.domain.Page;
import ru.mentee.power.crm.contact.domain.model.Person;

public interface ListPersonsUseCase {
  Page<Person> list(Query query);

  record Query(String email, int page, int size) {}
}
