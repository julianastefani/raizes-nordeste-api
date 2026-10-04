CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL,
                          perfil VARCHAR(50) NOT NULL
);

CREATE TABLE unidades (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          cidade VARCHAR(255) NOT NULL,
                          estado VARCHAR(2) NOT NULL
);

CREATE TABLE produtos (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          descricao VARCHAR(255),
                          preco NUMERIC(38, 2) NOT NULL
);

CREATE TABLE estoque_unidade (
                                 id BIGSERIAL PRIMARY KEY,
                                 unidade_id BIGINT NOT NULL,
                                 produto_id BIGINT NOT NULL,
                                 quantidade INTEGER NOT NULL,

                                 CONSTRAINT uk_estoque_unidade_produto
                                     UNIQUE (unidade_id, produto_id),

                                 CONSTRAINT fk_estoque_unidade
                                     FOREIGN KEY (unidade_id)
                                         REFERENCES unidades(id),

                                 CONSTRAINT fk_estoque_produto
                                     FOREIGN KEY (produto_id)
                                         REFERENCES produtos(id),

                                 CONSTRAINT ck_estoque_quantidade
                                     CHECK (quantidade >= 0)
);

CREATE TABLE pedidos (
                         id BIGSERIAL PRIMARY KEY,
                         cliente_id BIGINT NOT NULL,
                         unidade_id BIGINT NOT NULL,
                         canal_pedido VARCHAR(50) NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         valor_total NUMERIC(38, 2) NOT NULL,
                         criado_em TIMESTAMP
);

CREATE TABLE itens_pedido (
                              id BIGSERIAL PRIMARY KEY,
                              pedido_id BIGINT NOT NULL,
                              produto_id BIGINT NOT NULL,
                              quantidade INTEGER NOT NULL,
                              preco_unitario NUMERIC(10, 2) NOT NULL,

                              CONSTRAINT fk_item_pedido
                                  FOREIGN KEY (pedido_id)
                                      REFERENCES pedidos(id),

                              CONSTRAINT fk_item_produto
                                  FOREIGN KEY (produto_id)
                                      REFERENCES produtos(id),

                              CONSTRAINT ck_item_quantidade
                                  CHECK (quantidade > 0)
);

CREATE TABLE pagamentos (
                            id BIGSERIAL PRIMARY KEY,
                            pedido_id BIGINT NOT NULL,
                            valor NUMERIC(10, 2) NOT NULL,
                            status VARCHAR(50) NOT NULL,
                            criado_em TIMESTAMP NOT NULL,

                            CONSTRAINT fk_pagamento_pedido
                                FOREIGN KEY (pedido_id)
                                    REFERENCES pedidos(id)
);

CREATE TABLE fidelidade (
                            id BIGSERIAL PRIMARY KEY,
                            usuario_id BIGINT NOT NULL UNIQUE,
                            pontos INTEGER NOT NULL DEFAULT 0,
                            consentimento BOOLEAN NOT NULL DEFAULT FALSE,

                            CONSTRAINT fk_fidelidade_usuario
                                FOREIGN KEY (usuario_id)
                                    REFERENCES usuarios(id)
);

CREATE TABLE promocoes (
                           id BIGSERIAL PRIMARY KEY,
                           nome VARCHAR(255) NOT NULL,
                           descricao VARCHAR(255),
                           percentual_desconto NUMERIC(38, 2) NOT NULL,
                           data_inicio TIMESTAMP NOT NULL,
                           data_fim TIMESTAMP NOT NULL,
                           ativa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE auditoria (
                           id BIGSERIAL PRIMARY KEY,
                           acao VARCHAR(255) NOT NULL,
                           recurso VARCHAR(255) NOT NULL,
                           recurso_id BIGINT,
                           usuario VARCHAR(255),
                           data_hora TIMESTAMP NOT NULL
);