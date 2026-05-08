package ru.mentee.power.crm.contact.adapter.in.rest;

import lombok.Data;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.domain.model.PersonCompanyLink;

import java.util.UUID;

@Data
public class CompanyPersonResponse {
    private UUID personId;
    private String fullName;
    private String email;
    private String role;

    public static CompanyPersonResponse fromDomain(PersonCompanyLink link) {
        CompanyPersonResponse response = new CompanyPersonResponse();
        response.setPersonId(link.getPersonId());
        response.setRole(link.getRole());
        Person person = link.getPerson();
        if (person != null) {
            response.setFullName(person.getFullName());
            response.setEmail(person.getEmail());
        }
        return response;
    }
}
