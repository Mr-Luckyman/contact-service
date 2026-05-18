package ru.mentee.power.crm.contact.usecase.port.in;

import ru.mentee.power.crm.contact.domain.model.Invite;

public interface AcceptInviteUseCase {
  Invite accept(String referralCode, String email, String fullName);
}
