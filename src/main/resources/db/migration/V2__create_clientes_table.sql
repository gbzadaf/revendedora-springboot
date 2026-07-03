CREATE TABLE clientes (
                          id UUID PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          telefone VARCHAR(20),
                          endereco VARCHAR(255),
                          observacoes VARCHAR(500),
                          criado_em TIMESTAMP NOT NULL,
                          atualizado_em TIMESTAMP
);

CREATE INDEX idx_clientes_nome ON clientes (nome);