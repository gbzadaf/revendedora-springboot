CREATE TABLE itens_venda (
                             id UUID PRIMARY KEY,
                             venda_id UUID NOT NULL,
                             produto_id UUID NOT NULL,
                             quantidade INTEGER NOT NULL,
                             preco_unitario NUMERIC(10,2) NOT NULL,

                             CONSTRAINT fk_itens_venda_venda FOREIGN KEY (venda_id) REFERENCES vendas (id) ON DELETE CASCADE,
                             CONSTRAINT fk_itens_venda_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),

                             CONSTRAINT chk_itens_venda_quantidade_positiva CHECK (quantidade > 0)
);

CREATE INDEX idx_itens_venda_venda_id ON itens_venda (venda_id);
CREATE INDEX idx_itens_venda_produto_id ON itens_venda (produto_id);


/*fk_itens_venda_venda TEM ON DELETE CASCADE,porque, como na entidade Venda (orphanRemoval = true),
  item de venda não existe sozinho. Se a venda for deletada, os itens dela devem ir junto automaticamente.
  Isso espelha no banco exatamente o cascade = CascadeType.ALL do lado Java, senão haveria inconsistencia.
 */