# Revendedora API

API REST para gestão de revenda de cosméticos (Avon, Natura, Boticário, Demillus etc.), substituindo
o controle manual em caderno: estoque de produtos, vendas parceladas, clientes, parcelas e reservas
de produtos em falta.

## Stack

- **Java 21** + **Spring Boot 4.1.0**
- **Spring Data JPA** + **PostgreSQL** (produção) / **H2** (desenvolvimento)
- **Flyway** — versionamento de schema
- **Spring Security + JWT** — autenticação de usuário único
- **springdoc-openapi (Swagger)** — documentação interativa
- **JUnit 5 + Mockito** — testes unitários
- **Lombok**

## Domínio

| Entidade | Responsabilidade |
|---|---|
| `Produto` | Catálogo, preço e controle de estoque |
| `Cliente` | Cadastro de clientes |
| `Venda` | Venda parcelada, com itens e status derivado das parcelas |
| `ItemVenda` | Produto + quantidade + preço "congelado" no momento da venda |
| `Parcela` | Parcelamento da venda, com vencimento e status de pagamento |
| `Reserva` | Reserva de produto em falta no estoque, liberada automaticamente na reposição |
| `Usuario` | Login único do sistema (a revendedora) |

## Como rodar

```bash
# Ambiente de desenvolvimento (banco H2 em memória, sem dependências externas)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

A aplicação sobe em `http://localhost:8080`.

Para rodar com PostgreSQL real, defina as variáveis de ambiente `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
e use o profile `prod`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Autenticação

O sistema tem um único usuário (login `revendedora`), inserido via migration. Todos os endpoints
exigem token JWT, exceto o login.

```
POST /api/auth/login
{
  "login": "revendedora",
  "senha": "..."
}
```

A resposta traz o token a ser enviado em todas as demais requisições:

```
Authorization: Bearer {token}
```

## Documentação interativa

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

Use o botão **Authorize** para colar o token e testar os endpoints protegidos diretamente pela interface.

## Principais endpoints

```
POST   /api/auth/login

POST   /api/produtos
GET    /api/produtos
GET    /api/produtos/estoque-baixo
GET    /api/produtos/{id}
PUT    /api/produtos/{id}
DELETE /api/produtos/{id}
PATCH  /api/produtos/{id}/estoque        -> repõe estoque e libera reservas em fila automaticamente

POST   /api/clientes
GET    /api/clientes?nome=...
GET    /api/clientes/{id}
PUT    /api/clientes/{id}
DELETE /api/clientes/{id}

POST   /api/vendas                       -> cria venda, dá baixa no estoque, gera parcelas
GET    /api/vendas
GET    /api/vendas/{id}
GET    /api/vendas/cliente/{clienteId}
PATCH  /api/vendas/{id}/parcelas/{numero}/pagar

POST   /api/reservas                     -> só permitido se o produto estiver com estoque zerado
GET    /api/reservas
GET    /api/reservas/{id}
GET    /api/reservas/cliente/{clienteId}
PATCH  /api/reservas/{id}/cancelar
```

## Regras de negócio principais

- O preço de cada item de venda é sempre o preço **atual** do produto no momento da venda — nunca
  aceito diretamente do cliente da API, evitando manipulação de valores.
- Parcelas são geradas automaticamente a partir do valor total e do número de parcelas informado,
  com ajuste de centavos na última parcela para eliminar erro de arredondamento.
- O status da venda (`PENDENTE` → `PARCIALMENTE_PAGA` → `QUITADA`) é sempre recalculado a partir
  do estado real das parcelas.
- Reserva só é permitida quando o produto está com estoque zerado — havendo estoque disponível,
  a venda direta é priorizada.
- Ao repor estoque de um produto, as reservas aguardando esse produto são liberadas automaticamente,
  respeitando ordem de chegada (fila) e o limite da quantidade reposta.

## Testes

```bash
mvn test
```

Cobertura unitária concentrada nos Services com regra de negócio (`VendaService`, `ReservaService`),
com testes mais enxutos nos CRUDs simples (`ProdutoService`, `ClienteService`).
