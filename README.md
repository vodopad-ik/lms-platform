# LMS Platform 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Checkstyle](https://img.shields.io/badge/Checkstyle-Google%20Style-blue.svg)](https://checkstyle.org/)

Профессиональная платформа управления обучением (Learning Management System), разрабатываемая как комплексный проект по изучению Spring Boot и современной веб-разработки.

---

## 🛠 Стек технологий

*   **Core:** Java 21, Spring Boot 3
*   **Data:** Spring Data JPA, Hibernate, PostgreSQL, H2 (In-memory)
*   **API:** RESTful, Swagger/OpenAPI (Planned)
*   **Styling & Quality:** Google Checkstyle
*   **Tools:** Maven, Docker, Postman

---

## 📈 Дорожная карта и прогресс

### ✅ Этап 1: Basic REST Service (Завершено)
*   [x] Инициализация Spring Boot проекта.
*   [x] Реализация REST API для сущности `Course`.
*   [x] Эндпоинты с использованием `@RequestParam` и `@PathVariable`.
*   [x] Построение архитектуры: **Controller → Service → Repository**.
*   [x] Использование **DTO** и паттерна Mapper.
*   [x] Настройка **Checkstyle** (Google Style) для контроля качества кода.

### ⏳ Этап 2: JPA & Hibernate (В разработке)
*   [ ] Реализация модели данных из 5+ сущностей.
*   [ ] Настройка связей `OneToMany` и `ManyToMany`.
*   [ ] Реализация полного цикла CRUD.
*   [ ] Решение проблемы N+1 через `@EntityGraph` или `Fetch Join`.
*   [ ] Демонстрация работы транзакций (`@Transactional`).
*   [ ] Визуализация ER-диаграммы.

### 🚀 Будущие этапы
*   **Data Caching:** Пагинация, JPQL/Native запросы и собственный in-memory индекс.
*   **Error Handling:** Глобальный обработчик ошибок и логирование через AOP.
*   **Testing:** Unit-тесты с использованием Mockito и Stream API.
*   **Concurrency:** Асинхронные задачи и нагрузочное тестирование в JMeter.
*   **Client:** Разработка фронтенд-части (SPA).
*   **DevOps:** Docker-контейнеризация и GitHub CI/CD.

---

## 💻 Быстрый старт

### Требования
*   Java 21
*   Maven (или использование `./mvnw`)

### Запуск приложения
```bash
./mvnw spring-boot:run
```

### Тестирование API
Приложение запускается на порту `8080`.
*   **Список курсов:** `GET http://localhost:8080/api/courses`
*   **Поиск по ID:** `GET http://localhost:8080/api/courses/{id}`
*   **Поиск по названию:** `GET http://localhost:8080/api/courses/search?title=Название`

---

## 🏗 Архитектура проекта
Проект придерживается стандартной слоистой архитектуры для обеспечения высокой модульности:
*   `model` — Entity-классы, отражающие структуру БД.
*   `repository` — Интерфейсы для доступа к данным (Spring Data JPA).
*   `service` — Бизнес-логика и преобразование данных.
*   `dto` — Объекты для передачи данных через API.
*   `controller` — Обработка HTTP-запросов.
