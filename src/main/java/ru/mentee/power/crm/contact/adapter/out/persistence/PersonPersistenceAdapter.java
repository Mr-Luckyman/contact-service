package ru.mentee.power.crm.contact.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

@Component
@RequiredArgsConstructor
public class PersonPersistenceAdapter implements PersonRepository {

  private final PersonJpaRepository jpaRepository;
  private final PersonPersistenceMapper mapper;

  @Override
  public Person save(Person person) {
    PersonJpaEntity entity = mapper.toJpaEntity(person);
    PersonJpaEntity saved = jpaRepository.save(entity);
    return mapper.toDomainEntity(saved);
  }

  @Override
  public Optional<Person> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomainEntity);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }

  @Override
  public Page<Person> findAll(String email, Pageable pageable) {
    Page<PersonJpaEntity> entities;

    if (email != null && !email.isBlank()) {
      entities = jpaRepository.findByEmail(email, pageable);
    } else {
      entities = jpaRepository.findAll(pageable);
    }

    return entities.map(mapper::toDomainEntity);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }
}
