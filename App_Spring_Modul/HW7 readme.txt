Eureka - http://localhost:8761
user-service - http://localhost:8081/api/users
maildev - http://localhost:1080

конфиги находятся по адресу
https://github.com/c0ntary/config-repo

Создать пользователя
POST http://localhost:8080/api/users

{
  "name": "Пользователь",
  "email": "User@mail.com",
  "age": 30
}

Получить всех пользователей
GET http://localhost:8080/api/users

Получить пользователя по ID
GET http://localhost:8080/api/users/1

Обновить пользователя
PUT http://localhost:8080/api/users/1

{
  "name": "Новый Пользователь",
  "email": "Newuser@mail.com",
  "age": 31
}

Удалить пользователя (DELETE)
DELETE http://localhost:8080/api/users/1

тесты не валяться. сообщения на MailDev о создании, изменении и удалении приходят + скоректировал проект согласно рекомендациям


