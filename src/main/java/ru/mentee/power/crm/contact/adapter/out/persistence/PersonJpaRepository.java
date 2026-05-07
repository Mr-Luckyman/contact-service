package ru.mentee.power.crm.contact.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonJpaRepository extends JpaRepository<PersonJpaEntity, UUID> {
  Optional<PersonJpaEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
