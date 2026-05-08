package ru.mentee.power.crm.contact.adapter.in.rest;

import java.util.UUID;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(UUID id) {
        super("Company with id " + id + " not found");
    }
}
