package ru.mentee.power.crm.contact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mentee.power.crm.contact.usecase.port.out.CompanyRepository;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;
import ru.mentee.power.crm.contact.usecase.service.CompanyService;
import ru.mentee.power.crm.contact.usecase.service.PersonService;

@Configuration
public class BeanConfig {

  @Bean
  public PersonService personService(PersonRepository personRepository) {
    return new PersonService(personRepository);
  }

  @Bean
  public CompanyService companyService(
      CompanyRepository companyRepository, PersonRepository personRepository) {
    return new CompanyService(companyRepository, personRepository);
  }
}
