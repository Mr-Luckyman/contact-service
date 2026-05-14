package ru.mentee.power.crm.contact.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
  public Optional<Person> findByEmail(String email) {
    return jpaRepository.findByEmail(email).map(mapper::toDomainEntity);
  }

  @Override
  public List<Person> findAll(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return jpaRepository.findAll(pageRequest).stream().map(mapper::toDomainEntity).toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }
}
