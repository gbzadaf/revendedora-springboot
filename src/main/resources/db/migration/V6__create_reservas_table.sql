CREATE TABLE reservas (
                          id UUID PRIMARY KEY,
                          cliente_id UUID NOT NULL,
                          produto_id UUID NOT NULL,
                          quantidade INTEGER NOT NULL,
                          data_reserva TIMESTAMP NOT NULL,
                          status VARCHAR(20) NOT NULL DEFAULT 'AGUARDANDO',
                          criado_em TIMESTAMP NOT NULL,
                          atualizado_em TIMESTAMP,

                          CONSTRAINT fk_reservas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
                          CONSTRAINT fk_reservas_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),

                          CONSTRAINT chk_reservas_quantidade_positiva CHECK (quantidade > 0)
);

CREATE INDEX idx_reservas_cliente_id ON reservas (cliente_id);
CREATE INDEX idx_reservas_produto_id ON reservas (produto_id);
CREATE INDEX idx_reservas_status ON reservas (status);



/* idx_reservas_produto_id é o índice que representa: quando chega mercadoria nova, o sistema precisa perguntar
   rapidinho "existe alguma reserva AGUARDANDO pra esse produto?"
 */