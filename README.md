# LMS Platform 🚀

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Checkstyle](https://img.shields.io/badge/Checkstyle-Google%20Style-blue.svg)](https://checkstyle.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

Профессиональная платформа управления обучением (Learning Management System), разрабатываемая как комплексный проект по изучению Spring Boot и современной веб-разработки.

---

## 🛠 Стек технологий

*   **Core:** Java 21, Spring Boot 3
*   **Data:** Spring Data JPA, Hibernate, PostgreSQL
*   **Containerization:** Docker, Docker Compose
*   **Quality:** Google Checkstyle
*   **Tools:** Maven, Postman

---

## 📈 Дорожная карта и прогресс

### ✅ Этап 1: Basic REST Service (Завершено)
*   [x] Инициализация Spring Boot проекта.
*   [x] Реализация REST API для сущности `Course`.
*   [x] Эндпоинты с использованием `@RequestParam` и `@PathVariable`.
*   [x] Построение архитектуры: **Controller → Service → Repository**.
*   [x] Использование **DTO** и паттерна Mapper.
*   [x] Настройка **Checkstyle** (Google Style) для контроля качества кода.

### ✅ Этап 2: JPA & Hibernate (Завершено)
*   [x] Реализация модели данных из 5 сущностей: `Course`, `Teacher`, `Student`, `Lesson`, `Category`.
*   [x] Настройка связей:
    *   `OneToMany` (Teacher ↔ Course, Course ↔ Lesson)
    *   `ManyToMany` (Course ↔ Student)
    *   `ManyToOne` (Course ↔ Category)
*   [x] Реализация полного цикла CRUD (GET, POST, PUT, PATCH, DELETE).
*   [x] Настройка `CascadeType` и `FetchType`.
*   [x] Решение проблемы N+1 через `@EntityGraph` в `CourseRepository`.
*   [x] Демонстрация работы транзакций (`@Transactional` vs Non-transactional save).
*   [x] Автоматическая инициализация данных при запуске (`DataInitializer`).

### ⏳ Будущие этапы
*   **Data Caching:** Пагинация, JPQL/Native запросы и собственный in-memory индекс.
*   **Error Handling:** Глобальный обработчик ошибок и логирование через AOP.
*   **Testing:** Unit-тесты с использованием Mockito и Stream API.
*   **Concurrency:** Асинхронные задачи и нагрузочное тестирование в JMeter.
*   **DevOps:** GitHub CI/CD.

---

## 🗺 ER-диаграмма

```mermaid
erDiagram
    courses }o--|| categories : "category_id"
    courses }o--|| teachers : "teacher_id"
    courses ||--o{ lessons : "course_id"
    courses ||--o{ course_students : ""
    students ||--o{ course_students : ""

    courses {
        bigserial id PK
        varchar title
        text description
        bigint teacher_id FK
        bigint category_id FK
    }
    
    teachers {
        bigserial id PK
        varchar name
        varchar email
    }
    
    categories {
        bigserial id PK
        varchar name
    }
    
    students {
        bigserial id PK
        varchar name
        varchar email
    }
    
    lessons {
        bigserial id PK
        varchar title
        text content
        bigint course_id FK
    }
    
    course_students {
        bigint course_id FK
        bigint student_id FK
    }
```

---

## 💻 Быстрый старт

### Требования
*   Java 21
*   Docker & Docker Compose (для PostgreSQL)

### Запуск приложения
Приложение использует **Spring Boot Docker Compose**, поэтому база данных поднимется автоматически.
```bash
export DB_PASSWORD=your_password
./mvnw spring-boot:run
```

---

## 🧪 Тестирование API

### Курсы (Courses API)
*   `GET /api/courses` — Список всех курсов (оптимизировано через EntityGraph).
*   `GET /api/courses/{id}` — Детали курса.
*   `GET /api/courses/{courseId}/lessons` — Уроки курса.
*   `POST /api/courses` — Создание нового курса.
*   `POST /api/courses/{courseId}/lessons` — Добавление урока на курс.
*   `POST /api/courses/{courseId}/students/{studentId}` — Запись студента на курс.
*   `PUT /api/courses/{id}` — Полное обновление.
*   `PATCH /api/courses/{id}` — Частичное обновление.
*   `DELETE /api/courses/{id}` — Удаление.
*   `GET /api/courses/filter` — Фильтрация + пагинация (JPQL).
*   `GET /api/courses/filter/native` — Фильтрация + пагинация (native).

### Уроки (Lessons API)
*   `GET /api/lessons` — Список всех уроков.
*   `GET /api/lessons/{id}` — Детали урока.
*   `POST /api/lessons` — Создание урока (опционально `courseId` в body для привязки к курсу).
*   `PUT /api/lessons/{id}` — Обновление урока.
*   `DELETE /api/lessons/{id}` — Удаление урока.
*   `GET /api/lessons/filter` — Фильтрация + пагинация (JPQL).
*   `GET /api/lessons/filter/native` — Фильтрация + пагинация (native).

### Учителя (Teachers API)
*   `GET /api/teachers` — Список учителей.
*   `GET /api/teachers/{id}` — Детали учителя.
*   `POST /api/teachers` — Создание учителя.
*   `PUT /api/teachers/{id}` — Обновление учителя.
*   `DELETE /api/teachers/{id}` — Удаление учителя.
*   `GET /api/teachers/filter` — Фильтрация + пагинация (JPQL).
*   `GET /api/teachers/filter/native` — Фильтрация + пагинация (native).

### Студенты (Students API)
*   `GET /api/students` — Список студентов.
*   `GET /api/students/{id}` — Детали студента.
*   `POST /api/students` — Создание студента.
*   `PUT /api/students/{id}` — Обновление студента.
*   `DELETE /api/students/{id}` — Удаление студента.
*   `GET /api/students/filter` — Фильтрация + пагинация (JPQL).
*   `GET /api/students/filter/native` — Фильтрация + пагинация (native).

### Категории (Categories API)
*   `GET /api/categories` — Список категорий.
*   `GET /api/categories/{id}` — Детали категории.
*   `POST /api/categories` — Создание категории.
*   `PUT /api/categories/{id}` — Обновление категории.
*   `DELETE /api/categories/{id}` — Удаление категории.
*   `GET /api/categories/filter` — Фильтрация + пагинация (JPQL).
*   `GET /api/categories/filter/native` — Фильтрация + пагинация (native).

### Демонстрация транзакций
*   `POST /api/test/no-transaction` — Попытка сохранения без `@Transactional` (демонстрирует частичное сохранение при ошибке).
*   `POST /api/test/with-transaction` — Сохранение с `@Transactional` (демонстрирует полный откат при ошибке).
*   `GET /api/categories` — Просмотр списка категорий для проверки результатов тестов.

---

## 🏗 Архитектура проекта
*   `model` — JPA сущности.
*   `repository` — Интерфейсы Spring Data JPA.
*   `service` — Бизнес-логика.
*   `controller` — REST эндпоинты.
*   `dto` — Объекты передачи данных.
*   `mapper` — Преобразование Entity <-> DTO.
