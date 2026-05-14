package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.contact.domain.model.Invite;
import ru.mentee.power.crm.contact.usecase.port.in.AcceptInviteUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.CreateInviteUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.GetInviteUseCase;

@RestController
@RequestMapping("/api/v1/invites")
@RequiredArgsConstructor
public class InviteController {
  private final CreateInviteUseCase createInviteUseCase;
  private final GetInviteUseCase getInviteUseCase;
  private final AcceptInviteUseCase acceptInviteUseCase;

  @PostMapping
  public ResponseEntity<InviteResponse> createInvite(
      @Valid @RequestBody CreateInviteRequest request) {
    Invite invite =
        createInviteUseCase.create(
            request.getEmail(),
            request.getCompanyId(),
            request.getRole(),
            request.getInviterPersonId());
    return ResponseEntity.created(URI.create("/api/v1/invites/" + invite.getReferralCode()))
        .body(InviteResponse.fromDomain(invite));
  }

  @GetMapping("/{referralCode}")
  public ResponseEntity<InviteResponse> getInvite(@PathVariable String referralCode) {
    return ResponseEntity.ok(
        InviteResponse.fromDomain(getInviteUseCase.getByReferralCode(referralCode)));
  }

  @PostMapping("/{referralCode}/accept")
  public ResponseEntity<InviteResponse> acceptInvite(
      @PathVariable String referralCode, @Valid @RequestBody AcceptInviteRequest request) {
    Invite invite =
        acceptInviteUseCase.accept(referralCode, request.getEmail(), request.getFullName());
    return ResponseEntity.ok(InviteResponse.fromDomain(invite));
  }
}
