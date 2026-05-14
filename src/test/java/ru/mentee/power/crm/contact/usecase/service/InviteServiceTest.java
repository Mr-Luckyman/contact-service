package ru.mentee.power.crm.contact.usecase.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mentee.power.crm.contact.domain.model.Company;
import ru.mentee.power.crm.contact.domain.model.Invite;
import ru.mentee.power.crm.contact.domain.model.InviteStatus;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.out.CompanyRepository;
import ru.mentee.power.crm.contact.usecase.port.out.InviteRepository;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

class InviteServiceTest {
  private static final Instant NOW = Instant.parse("2026-05-14T10:00:00Z");

  @Mock private InviteRepository inviteRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private PersonRepository personRepository;
  @Mock private ReferralCodeGenerator referralCodeGenerator;

  private InviteService inviteService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    inviteService =
        new InviteService(
            inviteRepository,
            companyRepository,
            personRepository,
            referralCodeGenerator,
            Clock.fixed(NOW, ZoneOffset.UTC));
  }

  @Test
  void create_WithValidRequest_ReturnsPendingInvite() {
    UUID companyId = UUID.randomUUID();
    when(companyRepository.findById(companyId))
        .thenReturn(Optional.of(Company.create("Acme", List.of())));
    when(inviteRepository.findByEmailAndCompanyIdAndStatus(
            "ivan@example.com", companyId, InviteStatus.PENDING))
        .thenReturn(Optional.empty());
    when(referralCodeGenerator.generate()).thenReturn("ABCDEFGH");
    when(inviteRepository.existsByReferralCode("ABCDEFGH")).thenReturn(false);
    when(inviteRepository.save(any(Invite.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Invite result = inviteService.create("Ivan@Example.com", companyId, "CEO", null);

    assertEquals("ivan@example.com", result.getEmail());
    assertEquals("ABCDEFGH", result.getReferralCode());
    assertEquals(InviteStatus.PENDING, result.getStatus());
    assertEquals(NOW.plusSeconds(7L * 24 * 60 * 60), result.getExpiresAt());
  }

  @Test
  void create_WithActiveInvite_ThrowsConflict() {
    UUID companyId = UUID.randomUUID();
    Invite existing = Invite.create("ivan@example.com", companyId, "CEO", null, "ABCDEFGH", NOW);
    when(companyRepository.findById(companyId))
        .thenReturn(Optional.of(Company.create("Acme", List.of())));
    when(inviteRepository.findByEmailAndCompanyIdAndStatus(
            "ivan@example.com", companyId, InviteStatus.PENDING))
        .thenReturn(Optional.of(existing));

    assertThrows(
        InviteConflictException.class,
        () -> inviteService.create("ivan@example.com", companyId, "CEO", null));
  }

  @Test
  void getByReferralCode_WithExpiredPendingInvite_MarksExpiredAndThrowsGone() {
    UUID companyId = UUID.randomUUID();
    Invite invite =
        Invite.create(
            "ivan@example.com",
            companyId,
            "CEO",
            null,
            "ABCDEFGH",
            NOW.minusSeconds(8L * 24 * 60 * 60));
    when(inviteRepository.findByReferralCode("ABCDEFGH")).thenReturn(Optional.of(invite));
    when(inviteRepository.save(any(Invite.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    assertThrows(InviteExpiredException.class, () -> inviteService.getByReferralCode("ABCDEFGH"));

    verify(inviteRepository).save(any(Invite.class));
  }

  @Test
  void accept_WithExistingPerson_UsesExistingPersonAndAcceptsInvite() {
    UUID companyId = UUID.randomUUID();
    Person person = Person.create("Ivan Petrov", "ivan@example.com");
    Company company = Company.create("Acme", List.of());
    Invite invite = Invite.create("ivan@example.com", companyId, "CEO", null, "ABCDEFGH", NOW);
    when(inviteRepository.findByReferralCode("ABCDEFGH")).thenReturn(Optional.of(invite));
    when(personRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(person));
    when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
    when(companyRepository.save(any(Company.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(inviteRepository.save(any(Invite.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Invite result = inviteService.accept("ABCDEFGH", "ivan@example.com", "Ivan Petrov");

    assertEquals(InviteStatus.ACCEPTED, result.getStatus());
    assertEquals(1, company.getPersonLinks().size());
    assertEquals(person.getId(), company.getPersonLinks().getFirst().getPersonId());
  }

  @Test
  void accept_WithDifferentEmail_ThrowsBadRequest() {
    UUID companyId = UUID.randomUUID();
    Invite invite = Invite.create("ivan@example.com", companyId, "CEO", null, "ABCDEFGH", NOW);
    when(inviteRepository.findByReferralCode("ABCDEFGH")).thenReturn(Optional.of(invite));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> inviteService.accept("ABCDEFGH", "other@example.com", "Other Person"));

    assertEquals("email must match invite email", exception.getMessage());
  }

  @Test
  void accept_WithAcceptedInvite_ThrowsConflict() {
    UUID companyId = UUID.randomUUID();
    Invite invite =
        Invite.create("ivan@example.com", companyId, "CEO", null, "ABCDEFGH", NOW).accept(NOW);
    when(inviteRepository.findByReferralCode("ABCDEFGH")).thenReturn(Optional.of(invite));

    assertThrows(
        InviteConflictException.class,
        () -> inviteService.accept("ABCDEFGH", "ivan@example.com", "Ivan Petrov"));
  }
}
