package ru.mentee.power.crm.contact.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contact.domain.model.Company;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.domain.model.PersonCompanyLink;
import ru.mentee.power.crm.contact.usecase.port.out.CompanyRepository;

@Component
@RequiredArgsConstructor
public class CompanyPersistenceAdapter implements CompanyRepository {
  private final CompanyJpaRepository companyJpaRepository;
  private final PersonJpaRepository personJpaRepository;
  private final PersonCompanyLinkJpaRepository linkJpaRepository;

  @Override
  @Transactional
  public Company save(Company company) {
    CompanyJpaEntity savedCompany = companyJpaRepository.save(toCompanyJpaEntity(company));
    List<PersonCompanyLinkJpaEntity> savedLinks =
        company.getPersonLinks().stream()
            .map(link -> toLinkJpaEntity(link, savedCompany))
            .map(linkJpaRepository::save)
            .toList();
    return toDomain(savedCompany, savedLinks);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Company> findById(UUID id) {
    return companyJpaRepository
        .findById(id)
        .map(company -> toDomain(company, linkJpaRepository.findByCompany_Id(company.getId())));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Company> findAll(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return companyJpaRepository.findAll(pageRequest).stream()
        .map(company -> toDomain(company, linkJpaRepository.findByCompany_Id(company.getId())))
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsById(UUID id) {
    return companyJpaRepository.existsById(id);
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    companyJpaRepository.deleteById(id);
  }

  private CompanyJpaEntity toCompanyJpaEntity(Company company) {
    return new CompanyJpaEntity(
        company.getId(), company.getName(), company.getCreatedAt(), company.getUpdatedAt());
  }

  private PersonCompanyLinkJpaEntity toLinkJpaEntity(
      PersonCompanyLink link, CompanyJpaEntity company) {
    PersonJpaEntity person =
        personJpaRepository
            .findById(link.getPersonId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Person with id " + link.getPersonId() + " not found"));
    return new PersonCompanyLinkJpaEntity(
        link.getId(), person, company, link.getRole(), link.getCreatedAt(), link.getUpdatedAt());
  }

  private Company toDomain(CompanyJpaEntity company, List<PersonCompanyLinkJpaEntity> links) {
    return new Company(
        company.getId(),
        company.getName(),
        company.getCreatedAt(),
        company.getUpdatedAt(),
        links.stream().map(this::toDomainLink).toList());
  }

  private PersonCompanyLink toDomainLink(PersonCompanyLinkJpaEntity link) {
    Person person = toDomainPerson(link.getPerson());
    return PersonCompanyLink.restore(
        link.getId(),
        person.getId(),
        link.getCompany().getId(),
        link.getRole(),
        link.getCreatedAt(),
        link.getUpdatedAt(),
        person);
  }

  private Person toDomainPerson(PersonJpaEntity person) {
    return new Person(
        person.getId(),
        person.getFullName(),
        person.getEmail(),
        person.getCreatedAt(),
        person.getUpdatedAt());
  }
}
