package ru.mentee.power.crm.contact.usecase.port.in;

import ru.mentee.power.crm.contact.domain.model.Company;

import java.util.Optional;
import java.util.UUID;

public interface GetCompanyUseCase {
    Optional<Company> getById(UUID id);
}
