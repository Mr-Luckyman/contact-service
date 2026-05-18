package ru.mentee.power.crm.contact.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Invite {
  private UUID id;
  private String email;
  private UUID companyId;
  private String role;
  private UUID inviterPersonId;
  private String referralCode;
  private InviteStatus status;
  private Instant expiresAt;
  private Instant createdAt;
  private Instant updatedAt;

  public static Invite create(
      String email,
      UUID companyId,
      String role,
      UUID inviterPersonId,
      String referralCode,
      Instant now) {
    return Invite.builder()
        .id(UUID.randomUUID())
        .email(email)
        .companyId(companyId)
        .role(role)
        .inviterPersonId(inviterPersonId)
        .referralCode(referralCode)
        .status(InviteStatus.PENDING)
        .expiresAt(now.plusSeconds(7L * 24 * 60 * 60))
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  public boolean isPendingExpired(Instant now) {
    return status == InviteStatus.PENDING && !expiresAt.isAfter(now);
  }

  public Invite expire(Instant now) {
    return withStatus(InviteStatus.EXPIRED, now);
  }

  public Invite accept(Instant now) {
    return withStatus(InviteStatus.ACCEPTED, now);
  }

  private Invite withStatus(InviteStatus newStatus, Instant now) {
    return Invite.builder()
        .id(id)
        .email(email)
        .companyId(companyId)
        .role(role)
        .inviterPersonId(inviterPersonId)
        .referralCode(referralCode)
        .status(newStatus)
        .expiresAt(expiresAt)
        .createdAt(createdAt)
        .updatedAt(now)
        .build();
  }
}
