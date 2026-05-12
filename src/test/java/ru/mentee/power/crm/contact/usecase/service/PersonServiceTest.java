package ru.mentee.power.crm.contact.usecase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.mentee.power.crm.contact.adapter.in.rest.PersonNotFoundException;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.in.ListPersonsUseCase;
import ru.mentee.power.crm.contact.usecase.port.in.UpdatePersonUseCase;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

class PersonServiceTest {

  @Mock private PersonRepository personRepository;

  @InjectMocks private PersonService personService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void create_Success() {
    String fullName = "Иван Петров";
    String email = "ivan@example.com";
    String phone = "+79091234567";

    when(personRepository.existsByEmail(email)).thenReturn(false);

    Person person = Person.create(fullName, email, phone);
    when(personRepository.save(any(Person.class))).thenReturn(person);

    Person result = personService.create(fullName, email, phone);

    assertNotNull(result);
    assertEquals(fullName, result.getFullName());
    assertEquals(email, result.getEmail());
    assertEquals(phone, result.getPhone());
    verify(personRepository).existsByEmail(email);
    verify(personRepository).save(any(Person.class));
  }

  @Test
  void create_WithEmptyFullName_ThrowsException() {
    String fullName = "";
    String email = "ivan@example.com";

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> personService.create(fullName, email, null));

    assertEquals("fullName must not be blank", exception.getMessage());
    verify(personRepository, never()).existsByEmail(any());
    verify(personRepository, never()).save(any());
  }

  @Test
  void create_WithNullFullName_ThrowsException() {
    String email = "ivan@example.com";

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> personService.create(null, email, null));

    assertEquals("fullName must not be blank", exception.getMessage());
    verify(personRepository, never()).existsByEmail(any());
    verify(personRepository, never()).save(any());
  }

  @Test
  void create_WithEmptyEmail_ThrowsException() {
    String fullName = "Иван Петров";
    String email = "";

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> personService.create(fullName, email, null));

    assertEquals("email must not be blank", exception.getMessage());
    verify(personRepository, never()).existsByEmail(any());
    verify(personRepository, never()).save(any());
  }

  @Test
  void create_WithNullEmail_ThrowsException() {
    String fullName = "Иван Петров";

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> personService.create(fullName, null, null));

    assertEquals("email must not be blank", exception.getMessage());
    verify(personRepository, never()).existsByEmail(any());
    verify(personRepository, never()).save(any());
  }

  @Test
  void create_WithEmailConflict_ThrowsException() {
    String fullName = "Иван Петров";
    String email = "ivan@example.com";

    when(personRepository.existsByEmail(email)).thenReturn(true);

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class, () -> personService.create(fullName, email, null));

    assertEquals("Person with email " + email + " already exists", exception.getMessage());
    verify(personRepository).existsByEmail(email);
    verify(personRepository, never()).save(any());
  }

  @Test
  void getById_ExistingId_ReturnsPerson() {
    UUID id = UUID.randomUUID();
    Person person = Person.create("Иван Петров", "ivan@example.com", null);
    when(personRepository.findById(id)).thenReturn(Optional.of(person));

    Optional<Person> result = personService.getById(id);

    assertTrue(result.isPresent());
    assertEquals(person.getId(), result.get().getId());
    assertEquals(person.getFullName(), result.get().getFullName());
    assertEquals(person.getEmail(), result.get().getEmail());
    verify(personRepository).findById(id);
  }

  @Test
  void getById_NonExistingId_ReturnsEmpty() {
    UUID id = UUID.randomUUID();
    when(personRepository.findById(id)).thenReturn(Optional.empty());

    Optional<Person> result = personService.getById(id);

    assertFalse(result.isPresent());
    verify(personRepository).findById(id);
  }

  @Test
  void getById_NullId_ThrowsException() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> personService.getById(null));

    assertEquals("id must not be null", exception.getMessage());
    verify(personRepository, never()).findById(any());
  }

  @Test
  void update_Success_WithSameEmail() {
    UUID id = UUID.randomUUID();
    Person existingPerson = Person.create("Иван Петров", "ivan@example.com", null);
    UpdatePersonUseCase.UpdatePersonCommand command =
        new UpdatePersonUseCase.UpdatePersonCommand(
            "Иван Сидоров", "ivan@example.com", "+79091111111");

    when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
    when(personRepository.save(any(Person.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Person result = personService.update(id, command);

    assertEquals("Иван Сидоров", result.getFullName());
    assertEquals("ivan@example.com", result.getEmail());
    assertEquals("+79091111111", result.getPhone());
    verify(personRepository).findById(id);
    verify(personRepository).save(any(Person.class));
  }

  @Test
  void update_Success_WithNewEmail() {
    UUID id = UUID.randomUUID();
    Person existingPerson = Person.create("Иван Петров", "ivan@example.com", null);
    UpdatePersonUseCase.UpdatePersonCommand command =
        new UpdatePersonUseCase.UpdatePersonCommand("Иван Петров", "ivan.new@example.com", null);

    when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
    when(personRepository.existsByEmail("ivan.new@example.com")).thenReturn(false);
    when(personRepository.save(any(Person.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Person result = personService.update(id, command);

    assertEquals("ivan.new@example.com", result.getEmail());
    verify(personRepository).existsByEmail("ivan.new@example.com");
    verify(personRepository).save(any(Person.class));
  }

  @Test
  void update_WithNonExistingId_ThrowsException() {
    UUID id = UUID.randomUUID();
    UpdatePersonUseCase.UpdatePersonCommand command =
        new UpdatePersonUseCase.UpdatePersonCommand("Иван Петров", "ivan@example.com", null);

    when(personRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(PersonNotFoundException.class, () -> personService.update(id, command));
    verify(personRepository, never()).save(any());
  }

  @Test
  void update_WithEmailConflict_ThrowsException() {
    UUID id = UUID.randomUUID();
    Person existingPerson = Person.create("Иван Петров", "ivan@example.com", null);
    UpdatePersonUseCase.UpdatePersonCommand command =
        new UpdatePersonUseCase.UpdatePersonCommand("Иван Петров", "taken@example.com", null);

    when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
    when(personRepository.existsByEmail("taken@example.com")).thenReturn(true);

    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> personService.update(id, command));

    assertEquals("Person with email taken@example.com already exists", exception.getMessage());
    verify(personRepository, never()).save(any());
  }

  @Test
  void update_WithNullId_ThrowsException() {
    UpdatePersonUseCase.UpdatePersonCommand command =
        new UpdatePersonUseCase.UpdatePersonCommand("Иван Петров", "ivan@example.com", null);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> personService.update(null, command));

    assertEquals("id must not be null", exception.getMessage());
    verify(personRepository, never()).findById(any());
  }

  @Test
  void delete_Success() {
    UUID id = UUID.randomUUID();
    Person existingPerson = Person.create("Иван Петров", "ivan@example.com", null);

    when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
    doNothing().when(personRepository).deleteById(id);

    assertDoesNotThrow(() -> personService.delete(id));
    verify(personRepository).findById(id);
    verify(personRepository).deleteById(id);
  }

  @Test
  void delete_WithNonExistingId_ThrowsException() {
    UUID id = UUID.randomUUID();

    when(personRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(PersonNotFoundException.class, () -> personService.delete(id));
    verify(personRepository, never()).deleteById(any());
  }

  @Test
  void delete_WithNullId_ThrowsException() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> personService.delete(null));

    assertEquals("id must not be null", exception.getMessage());
    verify(personRepository, never()).findById(any());
  }

  @Test
  void list_Success_WithEmail() {
    String email = "ivan@example.com";
    ListPersonsUseCase.Query query = new ListPersonsUseCase.Query(email, 0, 20);
    Pageable pageable = PageRequest.of(0, 20);
    Page<Person> expectedPage = new PageImpl<>(List.of());

    when(personRepository.findAll(email, pageable)).thenReturn(expectedPage);

    Page<Person> result = personService.list(query);

    assertNotNull(result);
    verify(personRepository).findAll(email, pageable);
  }

  @Test
  void list_Success_WithoutEmail() {
    ListPersonsUseCase.Query query = new ListPersonsUseCase.Query(null, 0, 20);
    Pageable pageable = PageRequest.of(0, 20);
    Page<Person> expectedPage = new PageImpl<>(List.of());

    when(personRepository.findAll(null, pageable)).thenReturn(expectedPage);

    Page<Person> result = personService.list(query);

    assertNotNull(result);
    verify(personRepository).findAll(null, pageable);
  }

  @Test
  void list_WithSizeExceedingMax_ThrowsException() {
    ListPersonsUseCase.Query query = new ListPersonsUseCase.Query(null, 0, 101);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> personService.list(query));

    assertEquals("size must not exceed 100", exception.getMessage());
    verify(personRepository, never()).findAll(any(), any());
  }

  @Test
  void list_WithNegativePage_ThrowsException() {
    ListPersonsUseCase.Query query = new ListPersonsUseCase.Query(null, -1, 20);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> personService.list(query));

    assertEquals("page must be >= 0", exception.getMessage());
    verify(personRepository, never()).findAll(any(), any());
  }
}
