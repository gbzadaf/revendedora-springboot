# Sistema de Gestão para Revendedoras

API REST desenvolvida em Java com Spring Boot para auxiliar revendedoras de cosméticos (Avon, Boticário, Demillus e outras marcas) no gerenciamento do seu negócio.

## Sobre o Projeto

O sistema foi criado para resolver um problema real: revendedoras de cosméticos precisam controlar estoque, pedidos de clientes, pagamentos parciais e produtos que ainda precisam ser encomendados às marcas. Tudo isso de forma simples e organizada.

## Funcionalidades

- Cadastro de marcas e produtos por marca
- Controle de estoque com baixa automática ao registrar pedidos
- Cadastro de clientes
- Registro de pedidos com itens
- Controle de fiado — pagamentos parciais com atualização automática do status do pedido
- Lista de pedidos futuros — produtos solicitados por clientes que ainda não estão no estoque
- Autenticação com JWT — acesso protegido por token

## Tecnologias Utilizadas

- Java 25
- Spring Boot 4.0.5
- Spring Data JPA
- Spring Security + JWT (jjwt 0.12.3)
- PostgreSQL
- Maven

## Estrutura do Projeto

```
src/main/java/com/gabrielf/revendedora/
├── config/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── BrandController.java
│   ├── CustomerController.java
│   ├── FutureOrderController.java
│   ├── OrderController.java
│   ├── OrderItemController.java
│   ├── PaymentController.java
│   ├── ProductController.java
│   └── StockController.java
├── dto/
│   ├── AuthDTO.java
│   ├── BrandDTO.java
│   ├── CustomerDTO.java
│   ├── FutureOrderDTO.java
│   ├── OrderDTO.java
│   ├── OrderItemDTO.java
│   ├── PaymentDTO.java
│   └── StockDTO.java
├── exception/
│   ├── BusinessException.java
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── model/
│   ├── Brand.java
│   ├── Customer.java
│   ├── FutureOrder.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderStatus.java
│   ├── Payment.java
│   ├── PaymentMethod.java
│   ├── Product.java
│   ├── Stock.java
│   └── User.java
├── repository/
│   ├── BrandRepository.java
│   ├── CustomerRepository.java
│   ├── FutureOrderRepository.java
│   ├── OrderItemRepository.java
│   ├── OrderRepository.java
│   ├── PaymentRepository.java
│   ├── ProductRepository.java
│   ├── StockRepository.java
│   └── UserRepository.java
└── service/
    ├── BrandService.java
    ├── CustomerService.java
    ├── FutureOrderService.java
    ├── OrderItemService.java
    ├── OrderService.java
    ├── PaymentService.java
    ├── ProductService.java
    ├── StockService.java
    └── UserService.java
```

## Como Executar

### Pré-requisitos

- Java 25
- PostgreSQL instalado e rodando
- Maven

### Configuração do Banco de Dados

Crie um banco de dados no PostgreSQL:

```sql
CREATE DATABASE revendedora_db;
```

### Configuração do application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/revendedora_db
spring.datasource.username=postgres
spring.datasource.password=suasenha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

server.port=8080

jwt.secret=sua-chave-secreta-longa-aqui
jwt.expiration=86400000
```

### Executando

```bash
mvn spring-boot:run
```

## Endpoints

### Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /auth/register | Registrar usuário |
| POST | /auth/login | Login e geração de token |

### Marcas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /brands | Listar todas as marcas |
| GET | /brands/{id} | Buscar marca por ID |
| POST | /brands | Criar marca |
| PUT | /brands/{id} | Atualizar marca |
| DELETE | /brands/{id} | Deletar marca |

### Produtos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /products | Listar todos os produtos |
| GET | /products/{id} | Buscar produto por ID |
| POST | /products | Criar produto |
| PUT | /products/{id} | Atualizar produto |
| DELETE | /products/{id} | Deletar produto |

### Estoque

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /stocks | Listar estoque |
| GET | /stocks/{id} | Buscar item do estoque |
| POST | /stocks | Registrar estoque |
| PUT | /stocks/{id} | Atualizar estoque |
| DELETE | /stocks/{id} | Deletar item do estoque |

### Clientes

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /customers | Listar clientes |
| GET | /customers/{id} | Buscar cliente por ID |
| POST | /customers | Cadastrar cliente |
| PUT | /customers/{id} | Atualizar cliente |
| DELETE | /customers/{id} | Deletar cliente |

### Pedidos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /orders | Listar pedidos |
| GET | /orders/{id} | Buscar pedido por ID |
| POST | /orders | Criar pedido |
| PUT | /orders/{id} | Atualizar pedido |
| DELETE | /orders/{id} | Deletar pedido |

### Itens de Pedido

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /order-items | Listar itens |
| GET | /order-items/{id} | Buscar item por ID |
| POST | /order-items | Adicionar item ao pedido |
| DELETE | /order-items/{id} | Remover item do pedido |

### Pagamentos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /payments | Listar pagamentos |
| GET | /payments/{id} | Buscar pagamento por ID |
| POST | /payments | Registrar pagamento |
| DELETE | /payments/{id} | Remover pagamento |

### Pedidos Futuros

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /future-orders | Listar pedidos futuros |
| GET | /future-orders/{id} | Buscar pedido futuro por ID |
| POST | /future-orders | Criar pedido futuro |
| PUT | /future-orders/{id} | Atualizar pedido futuro |
| DELETE | /future-orders/{id} | Deletar pedido futuro |

## Autenticação

Todos os endpoints exceto `/auth/register` e `/auth/login` requerem autenticação via JWT.

Após o login, inclua o token no header de cada requisição:

```
Authorization: Bearer seu-token-aqui
```

## Regras de Negócio

- Ao adicionar um item a um pedido, o estoque é baixado automaticamente
- Se o estoque for insuficiente, a operação é bloqueada com erro 422
- O status do pedido é calculado automaticamente: `PENDING`, `PARTIAL` ou `PAID`
- Ao registrar um pagamento, o `amountPaid` e o status do pedido são atualizados automaticamente
- Ao remover um pagamento, o `amountPaid` é revertido e o status recalculado
- O preço do item é sempre o preço atual do produto no banco, não pode ser informado manualmente

## Autor

Gabriel Fonseca.
