CREATE TABLE vendas (
                        id UUID PRIMARY KEY,
                        cliente_id UUID NOT NULL,
                        data_venda TIMESTAMP NOT NULL,
                        valor_total NUMERIC(10,2) NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
                        criado_em TIMESTAMP NOT NULL,
                        atualizado_em TIMESTAMP,

                        CONSTRAINT fk_vendas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE INDEX idx_vendas_cliente_id ON vendas (cliente_id);
CREATE INDEX idx_vendas_status ON vendas (status);
CREATE INDEX idx_vendas_data_venda ON vendas (data_venda);