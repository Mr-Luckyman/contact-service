package ru.mentee.power.crm.contact.adapter.in.rest;

import lombok.Data;
import ru.mentee.power.crm.contact.domain.model.Person;

import java.time.Instant;
import java.util.UUID;

@Data
public class PersonResponse {
    private UUID id;
    private String fullName;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    public static PersonResponse fromDomain(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setFullName(person.getFullName());
        response.setEmail(person.getEmail());
        response.setCreatedAt(person.getCreatedAt());
        response.setUpdatedAt(person.getUpdatedAt());
        return response;
    }
}