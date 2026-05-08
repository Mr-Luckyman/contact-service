package ru.mentee.power.crm.contact.adapter.in.rest;

import lombok.Data;
import ru.mentee.power.crm.contact.domain.model.Company;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CompanyResponse {
    private UUID id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CompanyPersonResponse> persons;

    public static CompanyResponse fromDomain(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());
        response.setPersons(company.getPersonLinks() == null
                ? new ArrayList<>()
                : company.getPersonLinks().stream()
                .map(CompanyPersonResponse::fromDomain)
                .toList());
        return response;
    }
}
