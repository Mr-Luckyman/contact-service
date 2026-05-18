package ru.mentee.power.crm.contact.usecase.service;

import java.security.SecureRandom;

public class ReferralCodeGenerator {
  private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
  private static final int CODE_LENGTH = 8;

  private final SecureRandom random = new SecureRandom();

  public String generate() {
    StringBuilder code = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      code.append(ALPHABET[random.nextInt(ALPHABET.length)]);
    }
    return code.toString();
  }
}
