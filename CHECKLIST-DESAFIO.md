# Checklist do desafio

| Requisito | Implementação |
|---|---|
| Java 17+ | `pom.xml` com Java 17 |
| Spring Boot | Spring Boot 3.5.16 |
| Maven | `pom.xml` |
| H2 em memória | `application.yml` |
| Spring Data JPA | `CustomerRepository` |
| JdbcTemplate | `CustomerJdbcRepository#searchByName` |
| Native Query | `CustomerRepository#findByStatusNative` |
| Spring Security | `SecurityConfig` |
| Basic Authentication | `httpBasic` |
| USER / ADMIN | usuários e regras no `SecurityConfig` |
| Validação | `CustomerRequest` + Bean Validation |
| Tratamento de erros | `GlobalExceptionHandler` |
| Integração HTTP | `ScoreClient` com Spring `RestClient` |
| Timeout / indisponibilidade / resposta inválida | `ScoreClient` + `ScoreServiceException` |
| Configuração externalizada | `application.yml` + variáveis de ambiente |
| JUnit | testes em `src/test/java` |
| CRUD completo | `CustomerController` / `CustomerService` |
| Busca por nome | `GET /customers/search?name=` |
| Busca por status | `GET /customers?status=` |
| Consulta de score | `GET /customers/{id}/score` |
| Mock do serviço externo | WireMock em `docker-compose.yml` |
| README completo | `README.md` |
| Exemplos de API | README + coleção Postman |
