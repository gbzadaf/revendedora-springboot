CREATE TABLE produtos (
                          id UUID PRIMARY KEY,
                          nome VARCHAR(150) NOT NULL,
                          marca VARCHAR(20) NOT NULL,
                          preco NUMERIC(10,2) NOT NULL,
                          quantidade_estoque INTEGER NOT NULL DEFAULT 0,
                          quantidade_minima INTEGER DEFAULT 0,
                          criado_em TIMESTAMP NOT NULL,
                          atualizado_em TIMESTAMP
);

CREATE INDEX idx_produtos_marca ON produtos (marca);
CREATE INDEX idx_produtos_nome ON produtos (nome);

/* indices em marca e nome  pensando nos relatórios/filtros que podem ser feitos depois
   ("mostra só produtos da Natura", "busca produto pelo nome").
 */