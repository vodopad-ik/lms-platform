# LMS Platform - Demo Guide для Data Caching

## 🚀 Приложение запущено!

**API Base URL:** `http://localhost:8080`

---

## 📋 Тесты для демонстрации всех 5 требований

### 1️⃣ **JPQL Query с фильтрацией по вложенным сущностям**

```
GET /api/courses/filter?minPrice=100&maxPrice=200&page=0&size=5
```

**Что это делает:**

- Использует `@Query` с JPQL
- Фильтрует по: `minPrice`, `maxPrice`
- Поддерживает фильтрацию по `department` и `category`
- Возвращает Page<CourseDto>

**Пример ответа:**

```json
{
  "content": [
    {
      "id": 2,
      "title": "React.js Modern",
      "price": 120.0,
      "teacher": { "id": 3, "name": "Alice Smith" },
      "category": { "id": 1, "name": "Development" }
    }
  ],
  "totalElements": 3,
  "number": 0,
  "size": 5
}
```

---

### 2️⃣ **Native Query (SQL) - аналогичный результат**

```
GET /api/courses/filter/native?minPrice=100&maxPrice=200&page=0&size=5
```

**Что отличается:**

- Запрос написан на прямом SQL
- Использует `CourseProjection` для маппинга результатов
- Результат идентичен JPQL версии

---

### 3️⃣ **Пагинация**

Попробуйте разные страницы:

```
GET /api/courses/filter?minPrice=100&maxPrice=200&page=0&size=2
GET /api/courses/filter?minPrice=100&maxPrice=200&page=1&size=2
GET /api/courses/filter?minPrice=100&maxPrice=200&page=2&size=2
```

**Параметры пагинации:**

- `page` - номер страницы (0-based)
- `size` - количество элементов на странице
- `sort` - сортировка (опционально, например: `sort=price,desc`)

---

### 4️⃣ **In-Memory HashMap Кеш**

**Тестирование производительности:**

```bash
# Первый запрос (загружается из БД)time curl -s "http://localhost:8080/api/courses/filter?minPrice=100&maxPrice=200&page=0&size=3"
и

# Второй запрос (из кеша - должен быть БЫСТРЕЕ)
time curl -s "http://localhost:8080/api/courses/filter?minPrice=100&maxPrice=200&page=0&size=3"

# Запрос с ДРУГИМИ параметрами (новый кеш ключ)
time curl -s "http://localhost:8080/api/courses/filter?minPrice=200&maxPrice=300&page=0&size=3"
```

**Что кешируется:**

- Используется `CourseQueryCache` компонент
- Индекс: `HashMap<CourseQueryCacheKey, Page<CourseDto>>`
- Поддерживает одновременно JPQL и Native запросы

---

### 5️⃣ **Инвалидация кеша при изменении данных**

**Шаг 1: Выполните фильтрованный запрос**

```
GET /api/courses/filter?minPrice=100&maxPrice=200&page=0&size=10
```

Заметьте количество результатов, например: `"totalElements": 3`

**Шаг 2: Создайте новый курс**

```
POST /api/courses
Content-Type: application/json

{
  "title": "New Test Course",
  "description": "Test course in price range 100-200",
  "price": 130,
  "durationWeeks": 4,
  "teacherId": 1,
  "categoryId": 1
}
```

**Шаг 3: Повторите фильтрованный запрос**

```
GET /api/courses/filter?minPrice=100&maxPrice=200&page=0&size=10
```

**Результат:** `"totalElements"` увеличилось на 1! ✅

- Это доказывает что кеш был **инвалидирован**
- `invalidateCache()` вызывается автоматически

---

## 🔧 Дополнительные тесты

### Все курсы

```
GET /api/courses
```

### Курс по ID

```
GET /api/courses/1
```

### Поиск по названию

```
GET /api/courses/search?title=Java
```

### Обновить курс

```
PUT /api/courses/1
Content-Type: application/json

{
  "title": "Updated Title",
  "price": 199.99,
  "categoryId": 1,
  "teacherId": 1
}
```

### Удалить курс

```
DELETE /api/courses/5
```

---

## 📊 HTTP Статусы

| Операция         | Статус         |
| ---------------- | -------------- |
| GET (успех)      | 200 OK         |
| POST (создание)  | 201 Created    |
| PUT (обновление) | 200 OK         |
| DELETE           | 204 No Content |
| Не найдено       | 404 Not Found  |

---

## 🗂️ Код для справки

**Основные компоненты:**

1. **CourseRepository** (Repository)
   - `findWithFilters()` - JPQL @Query
   - `findWithFiltersNative()` - Native SQL

2. **CourseService** (Business Logic)
   - `searchCourses()` - использует CourseQueryCache
   - Все методы создания/обновления вызывают `invalidateCache()`

3. **CourseQueryCache** (In-Memory Index)
   - HashMap с синхронизацией
   - Ключ: `CourseQueryCacheKey`

4. **CourseQueryCacheKey** (Composite Key)
   - Правильная реализация `equals()` и `hashCode()`
   - Включает все параметры запроса

5. **CourseController** (REST API)
   - `/api/courses/filter` - JPQL
   - `/api/courses/filter/native` - Native Query

---

## ✅ Итоговая проверка

> Все 5 требований "Data Caching" реализованы и работают:
>
> - ✅ JPQL фильтрация по вложенным сущностям
> - ✅ Native Query версия
> - ✅ Пагинация (Pageable)
> - ✅ HashMap в-памяти индекс
> - ✅ Составной ключ (equals/hashCode)
> - ✅ Инвалидация кеша на изменение

Приложение готово к демонстрации! 🎉
