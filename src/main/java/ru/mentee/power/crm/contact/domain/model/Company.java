package ru.mentee.power.crm.contact.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Company {
    private UUID id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder.Default
    private List<PersonCompanyLink> personLinks = new ArrayList<>();

    public static Company create(String name, List<PersonCompanyLink> personLinks) {
        Instant now = Instant.now();
        UUID companyId = UUID.randomUUID();
        List<PersonCompanyLink> links = personLinks == null
                ? new ArrayList<>()
                : new ArrayList<>(personLinks);
        links.forEach(link -> link.setCompanyId(companyId));
        return Company.builder()
                .id(companyId)
                .name(name)
                .createdAt(now)
                .updatedAt(now)
                .personLinks(links)
                .build();
    }
}
