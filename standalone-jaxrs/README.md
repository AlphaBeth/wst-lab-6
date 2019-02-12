# Standalone exterminatus service

Standalone version of service - no application server is required, just build and run.

## Build and run

`mvn clean package`

`java -jar target/standalone-jaxws-VERSION-jar-with-dependencies.jar`

## Configuration

- `config.properties`

Common configuration of service (base server URL).

- `datasource.properties`

Properties for database connection, used by Hikari connection pool.

