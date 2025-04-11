# java-shareit
Сервис для шеринга вещей с возможностью бронирования, поиска и обработки запросов.

## Оглавление
- [Технологии](#%EF%B8%8F-технологии)
- [Функции](#-функции)
- [REST API](#%EF%B8%8F-rest-api)
- [Модели данных](#-модели-данных)
- [Валидация](#-валидация)
- [Запуск](#-запуск)
- [Схема БД](#-схема-бд)
- [Особенности](#-особенности)

## ⚙️ Технологии
- Java 11
- Spring Boot
- PostgreSQL
- Hibernate
- Maven
- Docker
- REST API

## 🎯 Функции
### Вещи
- Добавление/редактирование/поиск вещей
- Просмотр списка вещей владельца
- Поиск по названию и описанию

### Бронирования
- Создание бронирования (WAITING → APPROVED/REJECTED)
- Подтверждение/отклонение владельцем
- Просмотр истории бронирований

### Запросы
- Создание запроса на вещь
- Просмотр своих и чужих запросов
- Добавление вещей в ответ на запрос

### Дополнительно
- Пагинация для больших списков
- Комментарии к вещам после бронирования
- Микросервисная архитектура (gateway + server)

## 🛠️ REST API
### Пользователи
| Метод | Путь                | Действие                     |
|-------|---------------------|------------------------------|
| POST  | `/users`            | Создать пользователя         |
| PATCH | `/users/{userId}`   | Обновить пользователя        |
| GET   | `/users/{userId}`   | Получить пользователя по ID  |

### Вещи
| Метод | Путь                          | Действие                     |
|-------|-------------------------------|------------------------------|
| POST  | `/items`                      | Добавить вещь                |
| PATCH | `/items/{itemId}`             | Обновить вещь                |
| GET   | `/items/{itemId}`             | Получить вещь по ID          |
| GET   | `/items`                      | Вещи текущего пользователя   |
| GET   | `/items/search?text={query}`  | Поиск доступных вещей        |

### Бронирования
| Метод | Путь                                      | Действие                     |
|-------|-------------------------------------------|------------------------------|
| POST  | `/bookings`                               | Создать бронирование         |
| PATCH | `/bookings/{bookingId}?approved={bool}`  | Подтвердить/отклонить         |
| GET   | `/bookings/{bookingId}`                  | Получить бронирование по ID  |
| GET   | `/bookings?state={state}`                | Список бронирований           |
| GET   | `/bookings/owner?state={state}`          | Бронирования владельца        |

### Запросы
| Метод | Путь                          | Действие                     |
|-------|-------------------------------|------------------------------|
| POST  | `/requests`                   | Создать запрос               |
| GET   | `/requests`                   | Мои запросы с ответами       |
| GET   | `/requests/all?from&size`     | Все запросы с пагинацией     |
| GET   | `/requests/{requestId}`       | Запрос по ID                |

## 🗃️ Модели данных 
### Класс `User`
```java
public class User {
    private long id;
    private String name;
    private String email;
}
```

### Класс `Item`
```java
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
```

### Класс `Booking`
```java
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
```

### Класс `ItemRequest`
```java
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
```

## 🔍 Валидация
  - Владелец может редактировать только свои вещи
  - Бронирование доступно только для вещей с available=true
  - Комментарии могут оставлять только арендаторы
  - Даты бронирования: start < end и не в прошлом
  - Пагинация: from ≥ 0, size ≥ 1

## 🚀 Запуск

### Требования
- Docker
- Docker Compose
- Maven

1. Клонируйте репозиторий:
```bash
git clone https://github.com/JulUvarova/java-shareit.git
```

2. Соберите проект:

```bash
mvn clean package
```

3. Запустите сервисы:

```bash
docker-compose up
```

Сервисы:
  - Gateway: http://localhost:8080
  - Server: http://localhost:9090

Docker-контейнеры
  - shareit-gateway: Порт 8080
  - shareit-server: Порт 9090
  - PostgreSQL: Порт 5432
   

## 📊 Схема БД
DB Schema

Основные таблицы:
  - users - пользователи
  - items - вещи
  - bookings - бронирования
  - requests - запросы
  - comments - комментарии

DDL-скрипты: src/main/resources/schema.sql

## 🌟 Особенности
- Двухмодульная архитектура (Gateway + Server)
- Валидация запросов на уровне Gateway
- Пагинация для эндпоинтов с большими данными
- Подробная система статусов бронирования: WAITING, APPROVED, REJECTED, PAST, FUTURE
- Кастомные ошибки с HTTP-статусами:
    - 404 NOT FOUND - объект не найден
    - 400 BAD REQUEST - неверные параметры
    - 409 CONFLICT - конфликт данных
    - 500 INTERNAL SERVER ERROR - серверная ошибка
