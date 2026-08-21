# Course — API REST de E-commerce com Spring Boot

Projeto de estudo de uma API REST de e-commerce construída com **Spring Boot 4.1**, **Spring Data JPA** e **banco H2 em memória**. A aplicação modela o domínio clássico de uma loja virtual — usuários, pedidos, itens de pedido, produtos, categorias e pagamentos — expondo endpoints REST para consulta e manipulação dos dados.

---

## Tecnologias

| Tecnologia | Versão / Detalhe |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.0 |
| Spring Web MVC | starter `spring-boot-starter-webmvc` |
| Spring Data JPA | Hibernate como provedor |
| H2 Database | banco em memória (perfil `test`) |
| PostgreSQL | driver disponível para uso futuro |
| Maven | build via `mvnw` (wrapper incluso) |

---

## Modelo de domínio

```
User 1 ────< N Order 1 ───1 Payment
                │
                └──< N OrderItem >── N Product >──< N Category
```

- **User** — cliente da loja (`name`, `email`, `phone`, `password`) e sua lista de pedidos.
- **Order** — pedido com `moment` (instante), status e cliente associado. Calcula o total somando o subtotal dos itens.
- **OrderStatus** — enum com código numérico persistido no banco: `WAITING_PAIMENT(1)`, `PAID(2)`, `SHIPPED(3)`, `DELIVERED(4)`, `CANCELED(5)`.
- **OrderItem** — associação N-N entre `Order` e `Product` com atributos próprios (`quantity`, `price`), usando chave primária composta via `@EmbeddedId` (`OrderItemPK`).
- **Product** — produto com relação N-N para `Category` (tabela de junção `tb_product_category`).
- **Category** — categoria de produtos.
- **Payment** — pagamento em relação 1-1 com `Order`, compartilhando a chave primária via `@MapsId`.

Tabelas geradas: `tb_user`, `tb_order`, `tb_order_item`, `tb_product`, `tb_category`, `tb_product_category`, `tb_payment`.

---

## Arquitetura em camadas

O projeto segue a divisão clássica em três camadas:

```
resources/   → controladores REST (@RestController) — recebem a requisição HTTP
services/    → regras de negócio (@Service) — orquestram e lançam exceções de domínio
repositories/→ acesso a dados (JpaRepository) — CRUD gerado pelo Spring Data
entities/    → entidades JPA do domínio
```

Complementos:

- `config/TestConfig` — `CommandLineRunner` ativo no perfil `test` que popula o banco em memória com dados de exemplo (3 categorias, 5 produtos, 2 usuários, 3 pedidos, 4 itens e 1 pagamento) a cada inicialização.
- `resources/exceptions/` — tratamento global de erros.
- `services/exceptions/` — exceções de domínio.

---

## Tratamento de exceções

Um `@ControllerAdvice` (`ResourceExceptionHandler`) intercepta as exceções de domínio e devolve uma resposta JSON padronizada (`StandardError`) com `timeStamp`, `status`, `error`, `message` e `path`:

| Exceção | HTTP | Quando ocorre |
|---|---|---|
| `ResourceNotFoundException` | `404 Not Found` | id inexistente em `findById`, `update` ou `delete` — em qualquer recurso |
| `DatabaseException` | `400 Bad Request` | violação de integridade referencial ao deletar |

Exemplo de resposta de erro:

```json
{
  "timeStamp": "2026-08-20T18:22:41Z",
  "status": 404,
  "error": "Resource not found",
  "message": "Resource not found. Id: 99",
  "path": "/users/99"
}
```

---

## Endpoints

### Users — `/users`

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| `GET` | `/users` | Lista todos os usuários | `200 OK` |
| `GET` | `/users/{id}` | Busca usuário por id | `200 OK` / `404` |
| `POST` | `/users` | Insere novo usuário | `201 Created` + header `Location` |
| `PUT` | `/users/{id}` | Atualiza `name`, `email` e `phone` | `200 OK` / `404` |
| `DELETE` | `/users/{id}` | Remove usuário | `204 No Content` / `404` / `400` |

### Orders — `/orders`

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| `GET` | `/orders` | Lista todos os pedidos (com itens, pagamento e total) | `200 OK` |
| `GET` | `/orders/{id}` | Busca pedido por id | `200 OK` / `404` |

### Products — `/products`

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| `GET` | `/products` | Lista todos os produtos | `200 OK` |
| `GET` | `/products/{id}` | Busca produto por id | `200 OK` / `404` |

### Categories — `/categories`

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| `GET` | `/categories` | Lista todas as categorias | `200 OK` |
| `GET` | `/categories/{id}` | Busca categoria por id | `200 OK` / `404` |

> O CRUD completo (insert / update / delete) está implementado apenas para `User`. As demais entidades expõem somente as operações de leitura, mas todas devolvem `404` padronizado quando o id não existe.

---

## Como executar

Pré-requisitos: **JDK 25** instalado.

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080` com o perfil `test` ativo, criando o schema e populando os dados de seed automaticamente.

### Console do H2

Disponível em `http://localhost:8080/h2-console`:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| User | `sa` |
| Password | *(vazio)* |

### Build e testes

```bash
./mvnw clean package   # gera o jar em target/
./mvnw test            # executa os testes
```

---

## Exemplos de uso

```bash
# Listar usuários
curl http://localhost:8080/users

# Criar um usuário
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Bob Brown","email":"bob@gmail.com","phone":"977557755","password":"123456"}'

# Atualizar
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Maria Green","email":"maria.green@gmail.com","phone":"977557755"}'

# Deletar
curl -X DELETE http://localhost:8080/users/2

# Consultar um pedido (traz itens, subtotais e total)
curl http://localhost:8080/orders/1
```

---

## Estrutura do projeto

```
src/main/java/com/rafaelalves/course/
├── CourseApplication.java
├── config/
│   └── TestConfig.java              # seed do banco (perfil test)
├── entities/
│   ├── User.java  Order.java  OrderItem.java
│   ├── Product.java  Category.java  Payment.java
│   ├── enums/OrderStatus.java
│   └── pk/OrderItemPK.java          # chave composta de OrderItem
├── repositories/                    # JpaRepository de cada entidade
├── resources/                       # controladores REST
│   └── exceptions/
│       ├── ResourceExceptionHandler.java
│       └── StandardError.java
└── services/                        # regras de negócio
    └── exceptions/
        ├── ResourceNotFoundException.java
        └── DatabaseException.java
```

---

## Detalhes de implementação

- **Serialização JSON** — `@JsonIgnore` evita loops infinitos nas associações bidirecionais (`User.orders`, `Category.products`, `OrderItem.order`, `Payment.order`). `@JsonFormat` padroniza os instantes em ISO-8601 UTC.
- **Enum persistido como código** — `Order` guarda o status como `Integer` internamente, convertendo para `OrderStatus` nos getters/setters, o que mantém o banco estável mesmo se a ordem do enum mudar.
- **Chave composta** — `OrderItemPK` é `@Embeddable` e agrega as referências para `Order` e `Product`; `OrderItem` delega os acessores para dentro dela.
- **`update` sem carregar a entidade** — `UserService.update` usa `getReferenceById`, que retorna um proxy e só toca o banco no momento do `save`.
- **`findById` uniforme** — todos os services resolvem o `Optional` com `orElseThrow(() -> new ResourceNotFoundException(id))`, garantindo `404` consistente em vez de erro 500.

---

## Autor

Rafael Alves — projeto desenvolvido para estudo de Spring Boot e JPA.
