package ru.mentee.power.crm.contact.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mentee.power.crm.contact.usecase.port.out.CompanyRepository;
import ru.mentee.power.crm.contact.usecase.port.out.InviteRepository;
import ru.mentee.power.crm.contact.usecase.port.out.PersonRepository;
import ru.mentee.power.crm.contact.usecase.service.CompanyService;
import ru.mentee.power.crm.contact.usecase.service.InviteService;
import ru.mentee.power.crm.contact.usecase.service.PersonService;
import ru.mentee.power.crm.contact.usecase.service.ReferralCodeGenerator;

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

  @Bean
  public ReferralCodeGenerator referralCodeGenerator() {
    return new ReferralCodeGenerator();
  }

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public InviteService inviteService(
      InviteRepository inviteRepository,
      CompanyRepository companyRepository,
      PersonRepository personRepository,
      ReferralCodeGenerator referralCodeGenerator,
      Clock clock) {
    return new InviteService(
        inviteRepository, companyRepository, personRepository, referralCodeGenerator, clock);
  }
}
