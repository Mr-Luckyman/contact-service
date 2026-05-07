package ru.mentee.power.crm.contact.adapter.out.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.mentee.power.crm.contact.domain.model.Person;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonPersistenceMapper {

  PersonJpaEntity toJpaEntity(Person person);

  Person toDomainEntity(PersonJpaEntity entity);
}
