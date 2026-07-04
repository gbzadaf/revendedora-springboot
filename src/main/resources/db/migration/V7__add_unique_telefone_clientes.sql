ALTER TABLE clientes
    ALTER COLUMN telefone SET NOT NULL;

ALTER TABLE clientes
    ADD CONSTRAINT uq_clientes_telefone UNIQUE (telefone);