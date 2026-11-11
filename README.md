# Revendedora API

API REST para gestão de revenda de cosméticos (Avon, Natura, Boticário, Demillus etc.), substituindo
o controle manual em caderno: estoque de produtos, vendas parceladas, clientes, parcelas e reservas
de produtos em falta. Acompanha um frontend simples, só para ver a API funcionando (ver seção
"Frontend").

## Stack

- **Java 21** + **Spring Boot 4.1.0**
- **Spring Data JPA** + **PostgreSQL** (produção) / **H2** (desenvolvimento)
- **Flyway**: versionamento de schema
- **Spring Security + JWT**: autenticação de usuário único
- **springdoc-openapi (Swagger)**: documentação interativa
- **JUnit 5 + Mockito**: testes unitários
- **Lombok**
- **Frontend**: HTML, CSS e JavaScript puros (sem framework, sem build)

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

A aplicação sobe em `http://localhost:8080` (API e frontend no mesmo endereço).

Para rodar contra PostgreSQL real, defina as variáveis de ambiente `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
e use o profile `prod`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Frontend

> **Nota:** o frontend foi feito **100% com IA**, em **HTML, CSS e JavaScript puros**, de forma
> propositalmente bem simples. O objetivo é apenas **ver a API funcionando visualmente**, além do
> Postman ou Swagger. O foco do projeto é o backend.

Os arquivos ficam em `src/main/resources/static` e são servidos pelo próprio Spring Boot. Como API e
página estão na mesma origem, não é necessário configurar CORS.

Abra `http://localhost:8080/` e faça login. A interface tem três seções:

- **Produtos**: cadastro, edição, exclusão e reposição de estoque (com aviso das reservas liberadas).
  Produtos com estoque igual ou abaixo do mínimo ficam destacados.
- **Reservas**: criação (só de produtos com estoque zerado), filtro por situação, cancelamento e
  marcação como entregue. Reservas disponíveis aparecem no topo, destacadas.
- **Clientes e vendas**: cadastro e busca de clientes, coluna "Deve" com destaque para parcelas
  atrasadas e, ao abrir um cliente, o histórico de vendas, o pagamento de parcelas e o registro de
  nova venda com carrinho de itens.

```
static/
├── index.html
├── css/style.css
└── js/
    ├── api.js        # fetch centralizado (token e tratamento de erro)
    ├── auth.js       # login e logout
    ├── app.js        # telas e abas
    ├── ui.js         # formatação e utilitários
    ├── produtos.js
    ├── reservas.js
    ├── clientes.js
    └── vendas.js     # painel do cliente: vendas e parcelas
```

## Autenticação

O sistema tem um único usuário (login `revendedora`), inserido via migration. A senha desse usuário
é definida na migration `V8` e serve apenas para desenvolvimento: **troque-a antes de qualquer uso
real**. Todos os endpoints exigem token JWT, exceto o login.

```
POST /api/v1/auth/login
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

Todos sob o prefixo `/api/v1`.

```
POST   /auth/login

POST   /produtos
GET    /produtos
GET    /produtos/estoque-baixo
GET    /produtos/{id}
PUT    /produtos/{id}
DELETE /produtos/{id}
PATCH  /produtos/{id}/estoque        -> repõe estoque e libera reservas em fila automaticamente

POST   /clientes
GET    /clientes?nome=...
GET    /clientes/{id}
PUT    /clientes/{id}
DELETE /clientes/{id}

POST   /vendas                       -> cria venda, dá baixa no estoque, gera parcelas
GET    /vendas
GET    /vendas/{id}
GET    /vendas/cliente/{clienteId}
PATCH  /vendas/{id}/parcelas/{numero}/pagar

POST   /reservas                     -> só permitido se o produto estiver com estoque zerado
GET    /reservas
GET    /reservas/{id}
GET    /reservas/cliente/{clienteId}
PATCH  /reservas/{id}/cancelar
PATCH  /reservas/{id}/entregar       -> só vale para reservas DISPONIVEL
```

## Regras de negócio principais

- O preço de cada item de venda é sempre o preço **atual** do produto no momento da venda: nunca
  aceito diretamente do cliente da API, evitando manipulação de valores.
- Parcelas são geradas automaticamente a partir do valor total e do número de parcelas informado,
  com ajuste de centavos na última parcela para eliminar erro de arredondamento.
- O status da venda (`PENDENTE` → `PARCIALMENTE_PAGA` → `QUITADA`) é sempre recalculado a partir
  do estado real das parcelas.
- Reserva só é permitida quando o produto está com estoque zerado. Havendo estoque disponível,
  a venda direta é priorizada.
- Ao repor estoque de um produto, as reservas aguardando esse produto são liberadas automaticamente,
  respeitando ordem de chegada (fila) e o limite da quantidade reposta.
- Marcar uma reserva como entregue não mexe no estoque: a baixa acontece quando a venda é registrada.

## Limitações conhecidas

- Não existe cancelamento de venda (nem estorno de estoque).
- A entrega de uma reserva não gera a venda automaticamente: são dois passos separados.
- O status `ATRASADA` de parcela existe no modelo, mas nenhuma rotina o aplica. O frontend identifica
  atraso comparando o vencimento com a data de hoje.
- A data de pagamento de uma parcela é sempre a data em que o pagamento foi lançado.

## Testes

```bash
mvn test
```

Cobertura unitária concentrada nos Services com regra de negócio (`VendaService`, `ReservaService`),
com testes mais enxutos nos CRUDs simples (`ProdutoService`, `ClienteService`). O frontend não tem
testes automatizados.


