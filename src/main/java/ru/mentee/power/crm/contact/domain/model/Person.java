package ru.mentee.power.crm.contact.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Person {
    private final UUID id;
    private final String fullName;
    private final String email;
    private final Instant createdAt;
    private Instant updatedAt;

    public Person(UUID id, String fullName, String email, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Person create(String fullName, String email) {
        UUID nowId = UUID.randomUUID();
        Instant now = Instant.now();
        return new Person(nowId, fullName, email, now, now);
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public Person update(String newFullName, String newEmail) {
        return new Person(
                this.id,
                newFullName,
                newEmail,
                this.createdAt,
                Instant.now()
        );
    }
}