-- Unidades iniciais
INSERT INTO unidades (nome, cidade, estado)
VALUES
    ('Unidade Recife', 'Recife', 'PE'),
    ('Unidade Botucatu', 'Botucatu', 'SP');


-- Produtos iniciais
INSERT INTO produtos (nome, descricao, preco)
VALUES
    ('Carne de Sol com Mandioca',
     'Carne de sol acompanhada de mandioca',
     45.00),

    ('Baiao de Dois',
     'Baiao de dois tradicional',
     29.90),

    ('Cuscuz Nordestino',
     'Cuscuz nordestino tradicional',
     15.90);


-- Estoque inicial da Unidade Recife
INSERT INTO estoque_unidade (unidade_id, produto_id, quantidade)
VALUES
    (1, 1, 20),
    (1, 2, 20),
    (1, 3, 20);


-- Estoque inicial da Unidade Botucatu
INSERT INTO estoque_unidade (unidade_id, produto_id, quantidade)
VALUES
    (2, 1, 20),
    (2, 2, 20),
    (2, 3, 20);