package ru.mentee.power.crm.contact.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonCompanyLinkJpaRepository extends JpaRepository<PersonCompanyLinkJpaEntity, UUID> {
    List<PersonCompanyLinkJpaEntity> findByCompany_Id(UUID companyId);
}
