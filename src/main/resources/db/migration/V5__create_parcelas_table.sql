CREATE TABLE parcelas (
                          id UUID PRIMARY KEY,
                          venda_id UUID NOT NULL,
                          numero_parcela INTEGER NOT NULL,
                          valor NUMERIC(10,2) NOT NULL,
                          data_vencimento DATE NOT NULL,
                          data_pagamento DATE,
                          status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',

                          CONSTRAINT fk_parcelas_venda FOREIGN KEY (venda_id) REFERENCES vendas (id) ON DELETE CASCADE,

                          CONSTRAINT chk_parcelas_numero_positivo CHECK (numero_parcela > 0),
                          CONSTRAINT chk_parcelas_valor_positivo CHECK (valor > 0),
                          CONSTRAINT uq_parcelas_venda_numero UNIQUE (venda_id, numero_parcela)
);

CREATE INDEX idx_parcelas_venda_id ON parcelas (venda_id);
CREATE INDEX idx_parcelas_status ON parcelas (status);
CREATE INDEX idx_parcelas_data_vencimento ON parcelas (data_vencimento);