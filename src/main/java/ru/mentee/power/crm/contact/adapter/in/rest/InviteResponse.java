package ru.mentee.power.crm.contact.adapter.in.rest;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import ru.mentee.power.crm.contact.domain.model.Invite;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;

@Data
public class InviteResponse {
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

  public static InviteResponse fromDomain(Invite invite) {
    InviteResponse response = new InviteResponse();
    response.setId(invite.getId());
    response.setEmail(invite.getEmail());
    response.setCompanyId(invite.getCompanyId());
    response.setRole(invite.getRole());
    response.setInviterPersonId(invite.getInviterPersonId());
    response.setReferralCode(invite.getReferralCode());
    response.setStatus(invite.getStatus());
    response.setExpiresAt(invite.getExpiresAt());
    response.setCreatedAt(invite.getCreatedAt());
    response.setUpdatedAt(invite.getUpdatedAt());
    return response;
  }
}
