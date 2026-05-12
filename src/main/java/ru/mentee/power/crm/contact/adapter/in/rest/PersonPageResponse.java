package ru.mentee.power.crm.contact.adapter.in.rest;

import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;
import ru.mentee.power.crm.contact.domain.model.Person;

@Data
public class PersonPageResponse {
  private List<PersonResponse> items;
  private int page;
  private int size;
  private long total;

  public static PersonPageResponse fromPage(Page<Person> page, PersonMapper mapper) {
    PersonPageResponse response = new PersonPageResponse();
    response.setItems(page.getContent().stream().map(mapper::toResponse).toList());
    response.setPage(page.getNumber());
    response.setSize(page.getSize());
    response.setTotal(page.getTotalElements());
    return response;
  }
}
