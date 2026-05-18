package ru.mentee.power.crm.contact.usecase.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import ru.mentee.power.crm.contact.domain.model.Company;
import ru.mentee.power.crm.contact.domain.model.PersonCompanyLink;
import ru.mentee.power.crm.contact.usecase.port.in.CreateCompanyUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.DeleteCompanyUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetCompanyUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.ListCompaniesUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.UpdateCompanyUseCase;
import ru.mentee.power.crm.contact.usecase.port.out.CompanyRepository;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

@AllArgsConstructor
public class CompanyService
    implements CreateCompanyUseCase,
        GetCompanyUseCase,
        ListCompaniesUseCase,
        UpdateCompanyUseCase,
        DeleteCompanyUseCase {
  private static final int MAX_PAGE_SIZE = 100;

  private final CompanyRepository companyRepository;
  private final PersonRepository personRepository;

  @Override
  public Company create(String name, List<PersonLinkCommand> personLinks) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }

    List<PersonCompanyLink> links =
        Optional.ofNullable(personLinks).stream()
            .flatMap(List::stream)
            .map(this::toPersonCompanyLink)
            .toList();

    Company company = Company.create(name.trim(), links);
    return companyRepository.save(company);
  }

  @Override
  public Optional<Company> getById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    return companyRepository.findById(id);
  }

  @Override
  public List<Company> list(int page, int size) {
    if (page < 0) {
      throw new IllegalArgumentException("page must be greater than or equal to 0");
    }
    if (size < 1 || size > MAX_PAGE_SIZE) {
      throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
    }
    return companyRepository.findAll(page, size);
  }

  @Override
  public Company updateName(UUID id, String name) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }

    Company company = companyRepository.findById(id).orElseThrow(() -> new CompanyNotFound(id));
    return companyRepository.save(company.rename(name.trim()));
  }

  @Override
  public void delete(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (!companyRepository.existsById(id)) {
      throw new CompanyNotFound(id);
    }
    companyRepository.deleteById(id);
  }

  private PersonCompanyLink toPersonCompanyLink(PersonLinkCommand command) {
    Objects.requireNonNull(command, "person link must not be null");
    Objects.requireNonNull(command.personId(), "personId must not be null");

    String role = command.role();
    if (role == null || role.isBlank()) {
      throw new IllegalArgumentException("role must not be blank");
    }

    personRepository
        .findById(command.personId())
        .orElseThrow(() -> new LinkedPersonNotFoundException(command.personId()));

    return PersonCompanyLink.create(command.personId(), role.trim());
  }

  public static class CompanyNotFound extends RuntimeException {
    public CompanyNotFound(UUID id) {
      super("Company with id " + id + " not found");
    }
  }
}
