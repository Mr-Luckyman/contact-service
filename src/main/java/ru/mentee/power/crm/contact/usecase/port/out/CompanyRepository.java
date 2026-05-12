package ru.mentee.power.crm.contact.usecase.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Company;

public interface CompanyRepository {
  Company save(Company company);

  Optional<Company> findById(UUID id);

  List<Company> findAll(int page, int size);
}
