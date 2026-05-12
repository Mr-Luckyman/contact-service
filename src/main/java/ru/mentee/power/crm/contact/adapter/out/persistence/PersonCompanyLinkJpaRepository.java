package ru.mentee.power.crm.contact.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonCompanyLinkJpaRepository
    extends JpaRepository<PersonCompanyLinkJpaEntity, UUID> {
  List<PersonCompanyLinkJpaEntity> findByCompany_Id(UUID companyId);
}
