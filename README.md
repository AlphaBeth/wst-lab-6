# Web service technologies, Laboratory work #6

## Task description

> Необходимо выполнить задание из третьей работы, но с использованием
  REST-сервиса. Таблицу базы данных, а также код для работы с ней можно оставить
  без изменений.

## Requirements

- Java 8
- Maven 3+
- Glassfish 4.0
- Postgresql 9.3+

## Getting started

Start with typing 

`mvn clean install`

in project root directory.

## Project structure

The project consists of some modules:

- data-access -- all database-related code (entity classes, data access objects, utilities for query generation)
- exterminatus-service -- implementation of JAX-RS resource
- standalone-jaxrs -- standalone version of exterminatus service
- jaxrs-client -- console client for web service
- utils -- common utilities classes
