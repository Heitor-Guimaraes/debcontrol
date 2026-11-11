-- ============================================================
-- debcontrol - consultas para o VIDEO (MySQL Workbench)
-- Selecione a linha desejada e use Ctrl+Enter para executar só ela.
-- ============================================================
USE mercado_fiado;

-- [0] Estado inicial (mostrar antes de começar)
SELECT * FROM cliente ORDER BY id_cliente;

-- [1] INCLUSÃO - depois de salvar "Maria Teste" no Java
SELECT * FROM cliente WHERE cpf = '52998224725';

-- [2] CONSULTA - mesma busca que a tela faz no campo "Buscar"
SELECT * FROM cliente WHERE nome LIKE '%Maria%';

-- [3] ALTERAÇÃO - depois de editar o telefone/endereço no Java
SELECT id_cliente, nome, telefone, endereco FROM cliente WHERE cpf = '52998224725';

-- [4] EXCLUSÃO - depois de excluir no Java (deve voltar VAZIO)
SELECT * FROM cliente WHERE cpf = '52998224725';
SELECT COUNT(*) AS total_clientes FROM cliente;

-- [5] FALHA PROPOSITAL - tentar excluir Ana Souza (tem vendas)
SELECT id_venda, id_cliente, valor_total, status FROM venda_fiada WHERE id_cliente = 1;

-- [6] Bônus: venda + pagamento (se sobrar tempo)
SELECT * FROM venda_fiada ORDER BY id_venda DESC LIMIT 3;
SELECT * FROM pagamento ORDER BY id_pagamento DESC LIMIT 3;
SELECT * FROM vw_saldo_cliente ORDER BY id_cliente;
