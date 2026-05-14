package ru.mentee.power.crm.contact.usecase.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.contact.domain.model.Company;
import ru.mentee.power.crm.contact.domain.model.Invite;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.domain.model.PersonCompanyLink;
import ru.mentee.power.crm.contact.usecase.port.in.AcceptInviteUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.CreateInviteUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetInviteUseCase;
import ru.mentee.power.crm.contact.usecase.port.out.CompanyRepository;
import ru.mentee.power.crm.contact.usecase.port.out.InviteRepository;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

@RequiredArgsConstructor
public class InviteService implements CreateInviteUseCase, GetInviteUseCase, AcceptInviteUseCase {
  private static final int MAX_REFERRAL_CODE_ATTEMPTS = 10;

  private final InviteRepository inviteRepository;
  private final CompanyRepository companyRepository;
  private final PersonRepository personRepository;
  private final ReferralCodeGenerator referralCodeGenerator;
  private final Clock clock;

  @Override
  @Transactional
  public Invite create(String email, UUID companyId, String role, UUID inviterPersonId) {
    String normalizedEmail = normalizeEmail(email);
    validateCompanyId(companyId);
    validateRole(role);

    if (companyRepository.findById(companyId).isEmpty()) {
      throw new CompanyService.CompanyNotFound(companyId);
    }
    if (inviterPersonId != null && personRepository.findById(inviterPersonId).isEmpty()) {
      throw new LinkedPersonNotFoundException(inviterPersonId);
    }

    expirePendingInviteIfNeeded(normalizedEmail, companyId);
    inviteRepository
        .findByEmailAndCompanyIdAndStatus(normalizedEmail, companyId, InviteStatus.PENDING)
        .ifPresent(
            invite -> {
              throw new InviteConflictException(
                  "Active invite for email "
                      + normalizedEmail
                      + " and company "
                      + companyId
                      + " already exists");
            });

    Invite invite =
        Invite.create(
            normalizedEmail,
            companyId,
            role.trim(),
            inviterPersonId,
            nextReferralCode(),
            Instant.now(clock));
    return inviteRepository.save(invite);
  }

  @Override
  @Transactional(noRollbackFor = InviteExpiredException.class)
  public Invite getByReferralCode(String referralCode) {
    Invite invite = findInvite(referralCode);
    if (invite.isPendingExpired(Instant.now(clock))) {
      inviteRepository.save(invite.expire(Instant.now(clock)));
      throw new InviteExpiredException(referralCode);
    }
    return invite;
  }

  @Override
  @Transactional
  public Invite accept(String referralCode, String email, String fullName) {
    Invite invite = findInvite(referralCode);
    Instant now = Instant.now(clock);

    if (invite.isPendingExpired(now)) {
      inviteRepository.save(invite.expire(now));
      throw new InviteConflictException("Invite with referralCode " + referralCode + " is expired");
    }
    if (invite.getStatus() != InviteStatus.PENDING) {
      throw new InviteConflictException(
          "Invite with referralCode " + referralCode + " is " + invite.getStatus());
    }

    String normalizedEmail = normalizeEmail(email);
    if (!invite.getEmail().equals(normalizedEmail)) {
      throw new IllegalArgumentException("email must match invite email");
    }

    Person person =
        personRepository
            .findByEmail(normalizedEmail)
            .orElseGet(
                () ->
                    personRepository.save(
                        Person.create(validateFullName(fullName), normalizedEmail)));

    Company company =
        companyRepository
            .findById(invite.getCompanyId())
            .orElseThrow(() -> new CompanyService.CompanyNotFound(invite.getCompanyId()));
    company.addPersonLink(PersonCompanyLink.create(person.getId(), invite.getRole()));
    companyRepository.save(company);

    return inviteRepository.save(invite.accept(now));
  }

  private Invite findInvite(String referralCode) {
    if (referralCode == null || referralCode.isBlank()) {
      throw new IllegalArgumentException("referralCode must not be blank");
    }
    return inviteRepository
        .findByReferralCode(referralCode)
        .orElseThrow(() -> new InviteNotFoundException(referralCode));
  }

  private void expirePendingInviteIfNeeded(String email, UUID companyId) {
    inviteRepository
        .findByEmailAndCompanyIdAndStatus(email, companyId, InviteStatus.PENDING)
        .filter(invite -> invite.isPendingExpired(Instant.now(clock)))
        .ifPresent(invite -> inviteRepository.save(invite.expire(Instant.now(clock))));
  }

  private String nextReferralCode() {
    for (int attempt = 0; attempt < MAX_REFERRAL_CODE_ATTEMPTS; attempt++) {
      String code = referralCodeGenerator.generate();
      if (!inviteRepository.existsByReferralCode(code)) {
        return code;
      }
    }
    throw new IllegalStateException("Unable to generate unique referralCode");
  }

  private String normalizeEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email must not be blank");
    }
    return email.trim().toLowerCase();
  }

  private String validateFullName(String fullName) {
    if (fullName == null || fullName.isBlank()) {
      throw new IllegalArgumentException("fullName must not be blank");
    }
    return fullName.trim();
  }

  private void validateCompanyId(UUID companyId) {
    if (companyId == null) {
      throw new IllegalArgumentException("companyId must not be null");
    }
  }

  private void validateRole(String role) {
    if (role == null || role.isBlank()) {
      throw new IllegalArgumentException("role must not be blank");
    }
  }
}
