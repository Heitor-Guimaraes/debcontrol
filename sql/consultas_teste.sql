USE mercado_fiado;

SELECT * FROM cliente ORDER BY id_cliente;
SELECT * FROM funcionario ORDER BY id_funcionario;
SELECT * FROM forma_pagamento ORDER BY id_forma_pagamento;
SELECT * FROM venda_fiada ORDER BY id_venda;
SELECT * FROM pagamento ORDER BY id_pagamento;
SELECT * FROM cobranca ORDER BY id_cobranca;
SELECT * FROM vw_saldo_cliente ORDER BY id_cliente;

SELECT
    v.id_venda,
    c.nome AS cliente,
    f.nome AS funcionario,
    v.data_venda,
    v.data_vencimento,
    v.descricao_compra,
    v.valor_total,
    COALESCE(SUM(p.valor_pago), 0) AS total_pago,
    v.valor_total - COALESCE(SUM(p.valor_pago), 0) AS saldo_venda,
    v.status
FROM venda_fiada v
INNER JOIN cliente c ON c.id_cliente = v.id_cliente
INNER JOIN funcionario f ON f.id_funcionario = v.id_funcionario
LEFT JOIN pagamento p ON p.id_venda = v.id_venda
WHERE v.status <> 'CANCELADA'
GROUP BY
    v.id_venda, c.nome, f.nome, v.data_venda, v.data_vencimento,
    v.descricao_compra, v.valor_total, v.status
ORDER BY v.id_venda;

SELECT
    co.id_cobranca,
    cl.nome AS cliente,
    v.id_venda,
    f.nome AS funcionario,
    co.data_cobranca,
    co.meio_contato,
    co.resultado,
    co.proximo_contato
FROM cobranca co
INNER JOIN venda_fiada v ON v.id_venda = co.id_venda
INNER JOIN cliente cl ON cl.id_cliente = v.id_cliente
INNER JOIN funcionario f ON f.id_funcionario = co.id_funcionario
ORDER BY co.data_cobranca;

SELECT
    (SELECT COUNT(*) FROM cliente) AS total_clientes,
    (SELECT COUNT(*) FROM funcionario) AS total_funcionarios,
    (SELECT COUNT(*) FROM forma_pagamento) AS total_formas_pagamento,
    (SELECT COUNT(*) FROM venda_fiada) AS total_vendas,
    (SELECT COUNT(*) FROM pagamento) AS total_pagamentos,
    (SELECT COUNT(*) FROM cobranca) AS total_cobrancas;
