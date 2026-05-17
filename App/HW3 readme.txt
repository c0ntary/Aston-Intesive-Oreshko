пересобрал Арр т.к. для ДЗ3 требовался сервисный слой.
обновил зависимости для сервисного слоя 
сделал exceptions для тестов
сделал сами тесты:
UserDaoIntegrationTest  — интеграционные тесты с Hibernate + PostgreSQL Testcontainers
ConstraintViolationException
NoResultException / null — при findById
TransactionException
SQLGrammarException (если таблица не создана)

UserServiceTest — юнит‑тесты с Mockito
IllegalArgumentException
RuntimeException
ошибки валидации

обновил pom и hibernate.cfg

были проблемы с docker не хотел работать с Testcontainers (возможны проблемы) при тестах