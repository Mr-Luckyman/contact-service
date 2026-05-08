package ru.mentee.power.crm.contact.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Person {
    private UUID id;
    private String fullName;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    public static Person create(String fullName, String email) {
        Instant now = Instant.now();
        return Person.builder()
                .id(UUID.randomUUID())
                .fullName(fullName)
                .email(email)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Person update(String newFullName, String newEmail) {
        return Person.builder()
                .id(this.id)
                .fullName(newFullName)
                .email(newEmail)
                .createdAt(this.createdAt)
                .updatedAt(Instant.now())
                .build();
    }
}
