package ru.mentee.power.crm.contact.usecase.port.in;

import java.util.UUID;
import ru.mentee.power.crm.contact.domain.model.Invite;

public interface CreateInviteUseCase {
  Invite create(String email, UUID companyId, String role, UUID inviterPersonId);
}
