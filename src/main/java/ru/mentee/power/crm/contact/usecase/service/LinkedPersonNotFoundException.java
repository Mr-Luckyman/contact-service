package ru.mentee.power.crm.contact.usecase.service;

import java.util.UUID;

public class LinkedPersonNotFoundException extends RuntimeException {
  public LinkedPersonNotFoundException(UUID personId) {
    super("Person with id " + personId + " not found");
  }
}
