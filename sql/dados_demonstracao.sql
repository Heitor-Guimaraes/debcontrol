-- Dados opcionais de demonstração. Use preferencialmente em um banco recém-criado.
-- INSERT IGNORE nunca sobrescreve registros existentes, mas os vínculos deste script
-- assumem os IDs abaixo: em um banco já em uso, os dados podem não fazer sentido.
USE mercado_fiado;

START TRANSACTION;

INSERT IGNORE INTO cliente (id_cliente, nome, cpf, telefone, endereco, ativo) VALUES
    (1, 'Ana Souza', '12345678909', '(11) 98888-1001', 'Rua das Flores, 120', TRUE),
    (2, 'Bruno Lima', '23456789092', '(11) 97777-2002', 'Avenida Central, 450', TRUE),
    (3, 'Carla Mendes', '34567890175', '(11) 96666-3003', 'Rua do Mercado, 78', TRUE),
    (4, 'Diego Santos', '45678901249', '(11) 95555-4004', 'Rua Primavera, 32', TRUE);

INSERT IGNORE INTO funcionario (id_funcionario, nome, cargo, login, senha_hash, ativo) VALUES
    (1, 'Marcos Oliveira', 'CAIXA', 'marcos.oliveira', SHA2('Senha123', 256), TRUE),
    (2, 'Juliana Rocha', 'GERENTE', 'juliana.rocha', SHA2('Senha456', 256), TRUE);

INSERT IGNORE INTO forma_pagamento (id_forma_pagamento, nome, descricao, ativo) VALUES
    (1, 'DINHEIRO', 'Pagamento realizado em dinheiro', TRUE),
    (2, 'PIX', 'Pagamento realizado por PIX', TRUE),
    (3, 'CARTAO', 'Pagamento realizado com cartao', TRUE),
    (4, 'TRANSFERENCIA', 'Pagamento por transferencia bancaria', TRUE);

INSERT IGNORE INTO venda_fiada
    (id_venda, id_cliente, id_funcionario, data_venda, data_vencimento,
     descricao_compra, valor_total, status, observacao)
VALUES
    (1, 1, 1, '2026-09-10 10:15:00', '2026-10-10', 'Produtos de limpeza e alimentos', 180.00, 'PARCIAL', 'Cliente realizou pagamento parcial'),
    (2, 2, 1, '2026-09-15 14:30:00', '2026-10-15', 'Compra de alimentos', 90.00, 'QUITADA', 'Venda totalmente paga'),
    (3, 3, 2, '2026-08-20 09:45:00', '2026-09-20', 'Compra mensal', 250.00, 'ATRASADA', 'Cliente sera contatado'),
    (4, 4, 1, '2026-10-01 16:10:00', '2026-11-01', 'Produtos de higiene', 70.00, 'ABERTA', NULL),
    (5, 1, 2, '2026-09-25 11:00:00', '2026-10-25', 'Compra cancelada', 120.00, 'CANCELADA', 'Registro cancelado pelo gerente');

INSERT IGNORE INTO pagamento
    (id_pagamento, id_venda, id_funcionario, id_forma_pagamento,
     data_pagamento, valor_pago, observacao)
VALUES
    (1, 1, 1, 2, '2026-09-20 12:00:00', 50.00, 'Primeiro pagamento parcial'),
    (2, 1, 2, 1, '2026-10-02 17:20:00', 30.00, 'Segundo pagamento parcial'),
    (3, 2, 1, 2, '2026-09-25 15:40:00', 90.00, 'Quitacao total da venda');

INSERT IGNORE INTO cobranca
    (id_cobranca, id_venda, id_funcionario, data_cobranca,
     meio_contato, resultado, proximo_contato, observacao)
VALUES
    (1, 3, 2, '2026-09-22 10:00:00', 'WHATSAPP', 'Mensagem enviada; cliente visualizou', '2026-09-29', 'Aguardando retorno'),
    (2, 3, 2, '2026-09-29 14:15:00', 'TELEFONE', 'Cliente prometeu pagar na proxima semana', '2026-10-06', NULL),
    (3, 1, 1, '2026-10-05 09:30:00', 'PRESENCIAL', 'Cliente informou que fara novo pagamento', '2026-10-12', NULL);

COMMIT;
