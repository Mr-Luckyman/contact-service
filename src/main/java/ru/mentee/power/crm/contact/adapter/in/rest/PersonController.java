package ru.mentee.power.crm.contact.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.in.CreatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetPersonUseCase;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonController {

    private final CreatePersonUseCase createPersonUseCase;
    private final GetPersonUseCase getPersonUseCase;

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@RequestBody CreatePersonRequest request) {
        Person person = createPersonUseCase.create(request.getFullName(), request.getEmail());
        PersonResponse response = PersonResponse.fromDomain(person);
        return ResponseEntity
                .created(URI.create("/api/v1/persons/" + person.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getPerson(@PathVariable UUID id) {
        Person person = getPersonUseCase.getById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        return ResponseEntity.ok(PersonResponse.fromDomain(person));
    }
}