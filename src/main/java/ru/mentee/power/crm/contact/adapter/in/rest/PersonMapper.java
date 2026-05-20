package ru.mentee.power.crm.contact.adapter.in.rest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.mentee.power.crm.contact.domain.model.Person;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {

  PersonResponse toResponse(Person person);

  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  Person toDomain(CreatePersonRequest request);
}
