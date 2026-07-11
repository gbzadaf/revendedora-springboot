CREATE TABLE usuarios (
                          id UUID PRIMARY KEY,
                          login VARCHAR(50) NOT NULL,
                          senha VARCHAR(100) NOT NULL,

                          CONSTRAINT uq_usuarios_login UNIQUE (login)
);

INSERT INTO usuarios (id, login, senha) VALUES (
                                                   gen_random_uuid(),
                                                   'revendedora',
                                                   '$2b$10$8vB7PLJiFnuFnfklrzrKCeomcK8/S/QYS1Iu3vH8/h5ju2AXyDfHW'
                                               );