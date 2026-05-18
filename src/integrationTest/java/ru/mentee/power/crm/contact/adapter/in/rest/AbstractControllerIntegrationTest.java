package ru.mentee.power.crm.contact.adapter.in.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
abstract class AbstractControllerIntegrationTest {
  @LocalServerPort protected int port;

  @Autowired protected TestRestTemplate restTemplate;

  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:15")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  static {
    POSTGRES.start();
  }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  protected String url(String path) {
    return "http://localhost:" + port + path;
  }
}
