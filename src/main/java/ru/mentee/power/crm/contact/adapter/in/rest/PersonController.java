package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.DeletePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetPersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.ListPersonsUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.UpdatePersonUseCase;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

  private final CreatePersonUseCase createPersonUseCase;
  private final GetPersonUseCase getPersonUseCase;
  private final ListPersonsUseCase listPersonsUseCase;
  private final UpdatePersonUseCase updatePersonUseCase;
  private final DeletePersonUseCase deletePersonUseCase;
  private final PersonMapper personMapper;

  @PostMapping
  public ResponseEntity<PersonResponse> createPerson(
      @Valid @RequestBody CreatePersonRequest request) {
    Person person = createPersonUseCase.create(request.getFullName(), request.getEmail());
    PersonResponse response = personMapper.toResponse(person);
    return ResponseEntity.created(URI.create("/api/v1/persons/" + person.getId())).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PersonResponse> getPerson(@PathVariable UUID id) {
    Person person = getPersonUseCase.getById(id).orElseThrow(() -> new PersonNotFoundException(id));
    return ResponseEntity.ok(personMapper.toResponse(person));
  }

  @GetMapping
  public ResponseEntity<List<PersonResponse>> listPersons(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseEntity.ok(
        listPersonsUseCase.list(page, size).stream().map(personMapper::toResponse).toList());
  }

  @PutMapping("/{id}")
  public ResponseEntity<PersonResponse> updatePerson(
      @PathVariable UUID id, @Valid @RequestBody UpdatePersonRequest request) {
    Person person = updatePersonUseCase.update(id, request.getFullName(), request.getEmail());
    return ResponseEntity.ok(personMapper.toResponse(person));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {
    deletePersonUseCase.delete(id);
    return ResponseEntity.noContent().build();
  }
}
