package ru.mentee.power.crm.contact.adapter.in.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.mentee.power.crm.contact.usecase.port.in.CreateCompanyUseCase;

import java.util.List;
import java.util.UUID;

@Data
public class CreateCompanyRequest {
    @NotBlank(message = "name must not be blank")
    private String name;

    @Valid
    private List<PersonCompanyLinkRequest> persons;

    public List<CreateCompanyUseCase.PersonLinkCommand> toCommands() {
        if (persons == null) {
            return List.of();
        }
        return persons.stream()
                .map(person -> new CreateCompanyUseCase.PersonLinkCommand(person.getPersonId(), person.getRole()))
                .toList();
    }

    @Data
    public static class PersonCompanyLinkRequest {
        @NotNull(message = "personId must not be null")
        private UUID personId;

        @NotBlank(message = "role must not be blank")
        private String role;
    }
}
