package ru.mentee.power.crm.contact.usecase.port.in;

import ru.mentee.power.crm.contact.domain.model.Invite;

public interface GetInviteUseCase {
  Invite getByReferralCode(String referralCode);
}
