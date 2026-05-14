package ru.mentee.power.crm.contact.usecase.service;

public class InviteNotFoundException extends RuntimeException {
  public InviteNotFoundException(String referralCode) {
    super("Invite with referralCode " + referralCode + " not found");
  }
}
