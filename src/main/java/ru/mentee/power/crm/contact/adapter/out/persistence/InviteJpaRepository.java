package ru.mentee.power.crm.contact.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;

public interface InviteJpaRepository extends JpaRepository<InviteJpaEntity, UUID> {
  Optional<InviteJpaEntity> findByReferralCode(String referralCode);

  Optional<InviteJpaEntity> findByEmailAndCompanyIdAndStatus(
      String email, UUID companyId, InviteStatus status);

  boolean existsByReferralCode(String referralCode);
}
