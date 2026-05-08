package ru.mentee.power.crm.contact.usecase.port.in;

import ru.mentee.power.crm.contact.domain.model.Company;

import java.util.List;

public interface ListCompaniesUseCase {
    List<Company> list(int page, int size);
}
