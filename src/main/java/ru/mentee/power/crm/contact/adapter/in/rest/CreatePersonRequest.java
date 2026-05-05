package ru.mentee.power.crm.contact.adapter.in.rest;

import lombok.Data;

@Data
public class CreatePersonRequest {
    private String fullName;
    private String email;
}