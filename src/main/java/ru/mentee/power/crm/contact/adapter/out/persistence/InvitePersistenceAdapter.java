package ru.mentee.power.crm.contact.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.contact.domain.model.Invite;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;
import ru.mentee.power.crm.contact.usecase.port.out.InviteRepository;

@Component
@RequiredArgsConstructor
public class InvitePersistenceAdapter implements InviteRepository {
  private final InviteJpaRepository inviteJpaRepository;

  @Override
  public Invite save(Invite invite) {
    return toDomain(inviteJpaRepository.save(toJpa(invite)));
  }

  @Override
  public Optional<Invite> findByReferralCode(String referralCode) {
    return inviteJpaRepository.findByReferralCode(referralCode).map(this::toDomain);
  }

  @Override
  public Optional<Invite> findByEmailAndCompanyIdAndStatus(
      String email, UUID companyId, InviteStatus status) {
    return inviteJpaRepository
        .findByEmailAndCompanyIdAndStatus(email, companyId, status)
        .map(this::toDomain);
  }

  @Override
  public boolean existsByReferralCode(String referralCode) {
    return inviteJpaRepository.existsByReferralCode(referralCode);
  }

  private InviteJpaEntity toJpa(Invite invite) {
    return new InviteJpaEntity(
        invite.getId(),
        invite.getEmail(),
        invite.getCompanyId(),
        invite.getRole(),
        invite.getInviterPersonId(),
        invite.getReferralCode(),
        invite.getStatus(),
        invite.getExpiresAt(),
        invite.getCreatedAt(),
        invite.getUpdatedAt());
  }

  private Invite toDomain(InviteJpaEntity invite) {
    return new Invite(
        invite.getId(),
        invite.getEmail(),
        invite.getCompanyId(),
        invite.getRole(),
        invite.getInviterPersonId(),
        invite.getReferralCode(),
        invite.getStatus(),
        invite.getExpiresAt(),
        invite.getCreatedAt(),
        invite.getUpdatedAt());
  }
}
