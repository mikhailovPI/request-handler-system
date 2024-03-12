# Request handler system

**Программа предназначена для регистрации и обработки пользовательских заявок.**

<u>***Возможности приложения:***</u>
- Создание заявки;
- Отправление заявки оператору на рассмотрение;
- Просмотр всех заявок с возможностью сортировки по дате и пагинацией;
- Посмотреть заявку;
- Принять заявку;
- Отклонить заявку;
- Посмотреть список пользователей;
- Назначить права оператора.

**В приложении имеются следующие роли:**
- USER - имеет возможность создавать заявку, просматривать свои заявки с возможностью сортировки по дате и пагинацией,
  редактировать свою заявку (если находится в статусе "ЧЕРНОВИК") и отправлять заявку на рассмотрение;
- OPERATOR - имеет возможность просматривать все заявки в статусах - "ОТПРАВЛЕНО", "ПРИНЯТО" и "ОТКЛОНЕНО" с
  возможностью сортировки по дате и пагинацией, искать заявки пользователя по его имени с возможностью сортировки
  по дате и пагинацией, принимать заявки, отклонять заявки;
- EXECUTOR - имеет возможность просматривать все выполненные заявки и переносить заявку в статус - "ВЫПОЛНЕНО"
- ADMIN - имеет возможность смотреть список всех пользователей с возможностью сортировки по дате и пагинацией,
  искать пользователя по имени/части имени, назначать USER права OPERATOR, удалить пользователя;

**Используемый стек: Java 11, Spring Boot, Spring Data JPA, Spring Security, Hibernate, Maven, PostgreSQL, MapStruct**

<u>***Для запуска приложения необходимо:***</u>
- Склонировать репозиторий на ПК в необходимую папку;
- Создать БД со следующими свойствами:
  - HOST: localhost;
  - PORT: 5432;
  - user и password см. в
    [application.properties](
    https://github.com/mikhailovPI/request-handler-system/blob/develop/src/main/resources/application.properties);
- Запустить файл
  [schema.sql](https://github.com/mikhailovPI/request-handler-system/blob/develop/src/main/resources/db/schema.sql);
- Произвести запуск приложения (class ClaimRegistrarApplication);
- Запустить Postman на [localhost:8080](http://localhost:8080);
- Запустить коллекцию
  [тестов](
  https://github.com/mikhailovPI/request-handler-system/blob/develop/info/request-handler-system.postman_collection.json)
  в Postman. Коллекция тестов Postman содержит:
  - запросы на создание пользователей с определенными ролями;
  - запросы на регистрацию и обработку пользовательских заявок;
  - запросы на проверку исключений.

***Схема базы данных:***
>![schema_database.png](request-handler-system/info/schema_database.png)
>
***Данные для тестирования в Postman:***
[tests](
https://github.com/mikhailovPI/request-handler-system/blob/develop/info/request-handler-system.postman_collection.json)








