package ru.mentee.power.crm.contact.usecase.port.out;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Invite;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;

public interface InviteRepository {
  Invite save(Invite invite);

  Optional<Invite> findByReferralCode(String referralCode);

  Optional<Invite> findByEmailAndCompanyIdAndStatus(
      String email, UUID companyId, InviteStatus status);

  boolean existsByReferralCode(String referralCode);
}
