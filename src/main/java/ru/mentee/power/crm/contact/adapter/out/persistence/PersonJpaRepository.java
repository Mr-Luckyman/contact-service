package ru.mentee.power.crm.contact.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonJpaRepository extends JpaRepository<PersonJpaEntity, UUID> {
    Optional<PersonJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}