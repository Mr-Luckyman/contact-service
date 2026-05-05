package ru.mentee.power.crm.contact.usecase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_Success() {
        // Given
        String fullName = "Иван Петров";
        String email = "ivan@example.com";

        when(personRepository.existsByEmail(email)).thenReturn(false);

        Person person = Person.create(fullName, email);
        when(personRepository.save(any(Person.class))).thenReturn(person);

        // When
        Person result = personService.create(fullName, email);

        // Then
        assertNotNull(result);
        assertEquals(fullName, result.getFullName());
        assertEquals(email, result.getEmail());
        verify(personRepository).existsByEmail(email);
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void create_WithEmptyFullName_ThrowsException() {
        // Given
        String fullName = "";
        String email = "ivan@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> personService.create(fullName, email));

        assertEquals("fullName must not be blank", exception.getMessage());
        verify(personRepository, never()).existsByEmail(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void create_WithNullFullName_ThrowsException() {
        // Given
        String fullName = null;
        String email = "ivan@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> personService.create(fullName, email));

        assertEquals("fullName must not be blank", exception.getMessage());
        verify(personRepository, never()).existsByEmail(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void create_WithEmptyEmail_ThrowsException() {
        // Given
        String fullName = "Иван Петров";
        String email = "";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> personService.create(fullName, email));

        assertEquals("email must not be blank", exception.getMessage());
        verify(personRepository, never()).existsByEmail(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void create_WithNullEmail_ThrowsException() {
        // Given
        String fullName = "Иван Петров";
        String email = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> personService.create(fullName, email));

        assertEquals("email must not be blank", exception.getMessage());
        verify(personRepository, never()).existsByEmail(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void create_WithEmailConflict_ThrowsException() {
        // Given
        String fullName = "Иван Петров";
        String email = "ivan@example.com";

        when(personRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> personService.create(fullName, email));

        assertEquals("Person with email " + email + " already exists", exception.getMessage());
        verify(personRepository).existsByEmail(email);
        verify(personRepository, never()).save(any());
    }

    @Test
    void getById_ExistingId_ReturnsPerson() {
        // Given
        UUID id = UUID.randomUUID();
        Person person = Person.create("Иван Петров", "ivan@example.com");
        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        // When
        Optional<Person> result = personService.getById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(person.getId(), result.get().getId());
        assertEquals(person.getFullName(), result.get().getFullName());
        assertEquals(person.getEmail(), result.get().getEmail());
        verify(personRepository).findById(id);
    }

    @Test
    void getById_NonExistingId_ReturnsEmpty() {
        // Given
        UUID id = UUID.randomUUID();
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Person> result = personService.getById(id);

        // Then
        assertFalse(result.isPresent());
        verify(personRepository).findById(id);
    }

    @Test
    void getById_NullId_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> personService.getById(null));

        assertEquals("id must not be null", exception.getMessage());
        verify(personRepository, never()).findById(any());
    }
}