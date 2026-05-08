package ru.mentee.power.crm.contact.usecase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mentee.power.crm.contact.domain.model.Company;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.in.CreateCompanyUseCase;
import ru.mentee.power.crm.contact.usecase.port.out.CompanyRepository;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PersonRepository personRepository;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        companyService = new CompanyService(companyRepository, personRepository);
    }

    @Test
    void create_WithNameOnly_ReturnsCompany() {
        String name = "Acme";
        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Company result = companyService.create(name, List.of());

        assertNotNull(result.getId());
        assertEquals(name, result.getName());
        assertTrue(result.getPersonLinks().isEmpty());
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void create_WithExistingPersonLink_ReturnsCompanyWithRole() {
        UUID personId = UUID.randomUUID();
        Person person = Person.create("Ivan Petrov", "ivan@example.com");
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Company result = companyService.create(
                "Acme",
                List.of(new CreateCompanyUseCase.PersonLinkCommand(personId, "CEO"))
        );

        assertEquals(1, result.getPersonLinks().size());
        assertEquals(personId, result.getPersonLinks().getFirst().getPersonId());
        assertEquals("CEO", result.getPersonLinks().getFirst().getRole());
        assertEquals(result.getId(), result.getPersonLinks().getFirst().getCompanyId());
        verify(personRepository).findById(personId);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void create_WithBlankName_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> companyService.create("", List.of())
        );

        assertEquals("name must not be blank", exception.getMessage());
        verify(companyRepository, never()).save(any());
    }

    @Test
    void create_WithUnknownPerson_ThrowsException() {
        UUID personId = UUID.randomUUID();
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThrows(
                LinkedPersonNotFoundException.class,
                () -> companyService.create(
                        "Acme",
                        List.of(new CreateCompanyUseCase.PersonLinkCommand(personId, "CEO"))
                )
        );

        verify(companyRepository, never()).save(any());
    }

    @Test
    void getById_ExistingId_ReturnsCompany() {
        UUID id = UUID.randomUUID();
        Company company = Company.create("Acme", List.of());
        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        Optional<Company> result = companyService.getById(id);

        assertTrue(result.isPresent());
        assertEquals(company.getId(), result.get().getId());
        verify(companyRepository).findById(id);
    }

    @Test
    void getById_NonExistingId_ReturnsEmpty() {
        UUID id = UUID.randomUUID();
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Company> result = companyService.getById(id);

        assertFalse(result.isPresent());
        verify(companyRepository).findById(id);
    }

    @Test
    void list_WithInvalidPage_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> companyService.list(-1, 20)
        );

        assertEquals("page must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    void list_WithInvalidSize_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> companyService.list(0, 0)
        );

        assertEquals("size must be between 1 and 100", exception.getMessage());
    }
}
