# Contact Service

Микросервис управления контактами (Person, Company) для CRM-системы.

## Технологии

- Java 25
- Spring Boot 3.5.9
- PostgreSQL 15
- Liquibas
- MapStruct
- OpenAPI 3.0 (Swagger UI)

## Быстрый старт

### Запуск PostgreSQL через Docker Compose

```bash
  docker compose up -d
```

### Запуск приложения

```bash
  ./gradlew bootRun
```

Сервис будет доступен по адресу: `http://localhost:8081`

## API

### Swagger UI

После запуска открой в браузере:

```
    http://localhost:8081/swagger-ui/index.html
```

### Эндпоинты

| Метод  | URL                    | Описание                                       |
|--------|------------------------|------------------------------------------------|
| POST   | `/api/v1/persons`      | Создание контакта                              |
| GET    | `/api/v1/persons/{id}` | Получение контакта по ID                       |
| PUT    | `/api/v1/persons/{id}` | Обновление контакта                            |
| DELETE | `/api/v1/persons/{id}` | Удаление контакта                              |
| GET    | `/api/v1/persons`      | Список контактов (пагинация + фильтр по email) |

### Примеры запросов

**Создание контакта:**

```bash
  curl -X POST http://localhost:8081/api/v1/persons \\\n  -H "Content-Type: application/json" \\\n  -d '{"fullName":"Иван Петров","email":"ivan@example.com","phone":"+79091234567"}'
```

**Получение списка с пагинацией:**

```bash
  curl "http://localhost:8081/api/v1/persons?page=0&size=20"
```

**Фильтр по email:**

```bash
  curl "http://localhost:8081/api/v1/persons?email=ivan@example.com"
```

## Сборка и тесты

```bash
# Сборка проекта
  ./gradlew clean build

# Запуск только юнит-тестов
  ./gradlew test

# Запуск интеграционных тестов с Testcontainers
  ./gradlew integrationTest
```

## Структура проекта (Hexagonal Architecture)

```
src/main/java/ru/mentee/power/crm/contact/
├── domain/               # Чистый домен (без Spring/JPA)
├── usecase/              # Бизнес-логика
│   ├── port/             # Входные и выходные порты (интерфейсы)
│   └── service/          # Реализация use case'ов
└── adapter/              # Адаптеры
    ├── in/rest/          # REST-контроллеры (вход)
    └── out/persistence/  # JPA-адаптеры (выход)
```

## Проверка работоспособности

1. Запусти сервис и PostgreSQL
2. Открой Swagger UI: `http://localhost:8081/swagger-ui/index.html`
3. Создай несколько контактов через `POST /persons`
4. Получи список через `GET /persons` с пагинацией
5. Обнови контакт через `PUT /persons/{id}`
6. Удали контакт через `DELETE /persons/{id}`

## CI

GitHub Actions запускает сборку и тесты при каждом push и PR.