Переписал приложение под Spring Boot
приложение предоставляет:
REST API для CRUD‑операций над пользователями
консольный интерфейс (ConsoleRunner) для работы через терминал
Spring Data JPA для доступа к PostgreSQL
DTO + Mapper слой
валидацию и обработку ошибок

Приложение поддерживает два способа работы:
REST API — через HTTP запросы
Консольное меню — через CommandLineRunner

архитектура
ConsoleRunner → UserService → UserRepository → PostgreSQL
REST Controller → UserService → UserRepository → PostgreSQL

Обработка ошибок

Используется @RestControllerAdvice:
404 — пользователь не найден
409 — email уже существует
400 — некорректные данные
