user-service — принимает запросы на регистрацию пользователя, сохраняет данные в PostgreSQL и отправляет событие в Kafka.
notification-service — слушает Kafka, получает событие о регистрации и отправляет письмо пользователю через MailDev.

Приложение поднимается на:
user-service - http://localhost:8080
notification-service - http://localhost:8081
MailDev UI - http://localhost:1080

добавлял пользователей через Postman. ниже команды

Создать пользователя
POST http://localhost:8080/api/users

{
  "name": "Андрей",
  "email": "andrey@mail.com",
  "age": 30
}

Получить всех пользователей
GET http://localhost:8080/api/users

Получить пользователя по ID
GET http://localhost:8080/api/users/1

Обновить пользователя
PUT http://localhost:8080/api/users/1

{
  "name": "Новый Андрей",
  "email": "new@mail.com",
  "age": 31
}

Удалить пользователя (DELETE)
DELETE http://localhost:8080/api/users/1

тесты не валяться. сообщения на MailDev о создании, изменении и удалении приходят