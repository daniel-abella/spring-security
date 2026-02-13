# Spring Boot Authentication & Authorization (Example)

This sample Spring Boot project includes:

- Spring Data JPA
- H2 in-memory database
- Lombok
- Spring Security (HTTP Basic + in-memory user)

Quick start:

```bash
mvn spring-boot:run
```

Default credentials: `user` / `password`

H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:testdb`)

API example:

```bash
curl -u user:password http://localhost:8080/api/users
```
