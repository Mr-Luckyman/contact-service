package ru.mentee.power.crm.contact.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.contact.domain.model.Person;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PersonPersistenceAdapter implements PersonRepository {

    private final PersonJpaRepository jpaRepository;

    @Override
    public Person save(Person person) {
        PersonJpaEntity entity = toJpaEntity(person);
        PersonJpaEntity saved = jpaRepository.save(entity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<Person> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomainEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private PersonJpaEntity toJpaEntity(Person person) {
        return new PersonJpaEntity(
                person.getId(),
                person.getFullName(),
                person.getEmail(),
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }

    private Person toDomainEntity(PersonJpaEntity entity) {
        return new Person(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}