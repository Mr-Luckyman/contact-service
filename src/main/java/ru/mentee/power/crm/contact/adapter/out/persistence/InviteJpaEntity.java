package ru.mentee.power.crm.contact.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;

@Entity
@Table(name = "invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InviteJpaEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(name = "company_id", nullable = false)
  private UUID companyId;

  @Column(nullable = false)
  private String role;

  @Column(name = "inviter_person_id")
  private UUID inviterPersonId;

  @Column(name = "referral_code", nullable = false, unique = true, length = 8)
  private String referralCode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InviteStatus status;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
