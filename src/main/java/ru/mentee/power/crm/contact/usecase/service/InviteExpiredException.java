package ru.mentee.power.crm.contact.usecase.service;

public class InviteExpiredException extends RuntimeException {
  public InviteExpiredException(String referralCode) {
    super("Invite with referralCode " + referralCode + " is expired");
  }
}
