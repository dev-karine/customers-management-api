<<<<<<< HEAD
# customers-management-api
=======
# Customer Management API

Microsserviço desenvolvido para o desafio técnico de **Desenvolvedor Java Pleno**. A aplicação gerencia clientes via REST e consulta o score do cliente em um serviço HTTP externo.

## Tecnologias

- Java 17
- Spring Boot 3.5.16
- Maven
- Spring Web
- Spring Data JPA
- JdbcTemplate
- H2 em memória
- Spring Security + Basic Authentication
- Bean Validation
- JUnit 5 + Mockito + MockMvc
- WireMock via Docker para simular o serviço externo de score

## Decisões de implementação

A solução foi mantida propositalmente simples para um teste técnico, separando `controller`, `service`, `repository` e `integration`. O CRUD usa Spring Data JPA; o filtro por status usa **Native Query**; a busca por nome usa **JdbcTemplate**, atendendo explicitamente aos requisitos do desafio.

A integração de score usa o `RestClient` do Spring e possui `connect timeout` e `read timeout`. Erros do serviço externo são traduzidos para respostas HTTP coerentes:

- `502 Bad Gateway`: resposta inválida ou erro inesperado do serviço externo;
- `503 Service Unavailable`: indisponibilidade/erro 5xx do serviço externo;
- `504 Gateway Timeout`: timeout de comunicação.

## Requisitos para execução

- JDK 17 ou superior
- Maven 3.6.3 ou superior
- Docker + Docker Compose (somente para subir o mock de score recomendado)

## Como iniciar o serviço de score simulado

Na raiz do projeto:

```bash
docker compose up -d score-mock
```

O WireMock ficará disponível em `http://localhost:9090` e responderá `GET /scores/{cpf}`.

Exemplo:

```bash
curl http://localhost:9090/scores/12345678901
```

Resposta esperada:

```json
{
  "cpf": "12345678901",
  "score": 750,
  "classification": "LOW_RISK"
}
```

## Como iniciar a aplicação

```bash
mvn spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

Também é possível gerar o JAR:

```bash
mvn clean package
java -jar target/customer-management-api-0.0.1-SNAPSHOT.jar
```

## Usuários de teste

| Perfil | Usuário | Senha | Permissões |
|---|---|---|---|
| USER | `user` | `user123` | consultas |
| ADMIN | `admin` | `admin123` | consultas + criação + alteração + exclusão |

## Endpoints

| Método | Endpoint | Perfil | Descrição |
|---|---|---|---|
| POST | `/customers` | ADMIN | cadastrar cliente |
| PUT | `/customers/{id}` | ADMIN | alterar cliente |
| DELETE | `/customers/{id}` | ADMIN | excluir cliente |
| GET | `/customers/{id}` | USER/ADMIN | consultar cliente por id |
| GET | `/customers` | USER/ADMIN | listar clientes |
| GET | `/customers?status=ACTIVE` | USER/ADMIN | filtrar por status - **Native Query** |
| GET | `/customers/search?name=joao` | USER/ADMIN | buscar por nome - **JdbcTemplate** |
| GET | `/customers/{id}/score` | USER/ADMIN | consultar score externo pelo CPF do cliente |

## Exemplos de utilização

### Criar cliente

```bash
curl -i -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{"name":"Joao da Silva","cpf":"12345678901","email":"joao@email.com","status":"ACTIVE"}' \
  http://localhost:8080/customers
```

### Listar clientes

```bash
curl -u user:user123 http://localhost:8080/customers
```

### Buscar por status

```bash
curl -u user:user123 "http://localhost:8080/customers?status=ACTIVE"
```

### Buscar por nome

```bash
curl -u user:user123 "http://localhost:8080/customers/search?name=joao"
```

### Consultar score

```bash
curl -u user:user123 http://localhost:8080/customers/1/score
```

## Validações e erros tratados

- campos obrigatórios ausentes -> `400 Bad Request`;
- CPF diferente de 11 dígitos -> `400 Bad Request`;
- e-mail inválido -> `400 Bad Request`;
- CPF já cadastrado -> `409 Conflict`;
- cliente inexistente -> `404 Not Found`;
- usuário não autenticado -> `401 Unauthorized`;
- USER tentando criar/alterar/excluir -> `403 Forbidden`;
- falha/indisponibilidade no score -> `502/503`;
- timeout no score -> `504 Gateway Timeout`.

## Configurações externalizadas

| Variável | Padrão |
|---|---|
| `SERVER_PORT` | `8080` |
| `SCORE_SERVICE_BASE_URL` | `http://localhost:9090` |
| `SCORE_SERVICE_CONNECT_TIMEOUT` | `1s` |
| `SCORE_SERVICE_READ_TIMEOUT` | `2s` |
| `APP_USER_USERNAME` | `user` |
| `APP_USER_PASSWORD` | `user123` |
| `APP_ADMIN_USERNAME` | `admin` |
| `APP_ADMIN_PASSWORD` | `admin123` |

## H2 Console

Disponível em `http://localhost:8080/h2-console`.

- JDBC URL: `jdbc:h2:mem:customersdb`
- User: `sa`
- Password: em branco

## Como executar os testes

```bash
mvn test
```

Os testes cobrem regras de negócio, segurança, JdbcTemplate, Native Query e a integração HTTP com score.

## Postman

Coleção pronta para importação:

`postman/customer-management-api.postman_collection.json`
>>>>>>> 91d0899 (implement api customer management)
