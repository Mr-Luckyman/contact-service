package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.List;
import ru.mentee.power.crm.contact.domain.model.Person;

public interface ListPersonsUseCase {
  List<Person> list(int page, int size);
}
