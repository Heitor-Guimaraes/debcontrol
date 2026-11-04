CREATE DATABASE IF NOT EXISTS mercado_fiado
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mercado_fiado;

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL,
    endereco VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS funcionario (
    id_funcionario BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    cargo VARCHAR(40) NOT NULL,
    login VARCHAR(80) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS forma_pagamento (
    id_forma_pagamento BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(40) NOT NULL UNIQUE,
    descricao VARCHAR(150),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS venda_fiada (
    id_venda BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    data_venda DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_vencimento DATE NOT NULL,
    descricao_compra VARCHAR(255) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    status ENUM('ABERTA', 'PARCIAL', 'QUITADA', 'ATRASADA', 'CANCELADA')
        NOT NULL DEFAULT 'ABERTA',
    observacao VARCHAR(255),
    CONSTRAINT fk_venda_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_venda_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario),
    CONSTRAINT chk_venda_valor CHECK (valor_total > 0),
    CONSTRAINT chk_venda_datas CHECK (
        data_vencimento >= DATE(data_venda)
        AND data_vencimento <= DATE_ADD(DATE(data_venda), INTERVAL 1 MONTH)
    ),
    INDEX idx_venda_cliente (id_cliente),
    INDEX idx_venda_funcionario (id_funcionario),
    INDEX idx_venda_status_vencimento (status, data_vencimento)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS pagamento (
    id_pagamento BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venda BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    id_forma_pagamento BIGINT NOT NULL,
    data_pagamento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_pago DECIMAL(10,2) NOT NULL,
    observacao VARCHAR(255),
    CONSTRAINT fk_pagamento_venda
        FOREIGN KEY (id_venda) REFERENCES venda_fiada(id_venda),
    CONSTRAINT fk_pagamento_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario),
    CONSTRAINT fk_pagamento_forma
        FOREIGN KEY (id_forma_pagamento) REFERENCES forma_pagamento(id_forma_pagamento),
    CONSTRAINT chk_pagamento_valor CHECK (valor_pago > 0),
    INDEX idx_pagamento_venda (id_venda),
    INDEX idx_pagamento_funcionario (id_funcionario),
    INDEX idx_pagamento_forma (id_forma_pagamento)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cobranca (
    id_cobranca BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_venda BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    data_cobranca DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    meio_contato ENUM('TELEFONE', 'WHATSAPP', 'PRESENCIAL', 'OUTRO') NOT NULL,
    resultado VARCHAR(150) NOT NULL,
    proximo_contato DATE,
    observacao VARCHAR(255),
    CONSTRAINT fk_cobranca_venda
        FOREIGN KEY (id_venda) REFERENCES venda_fiada(id_venda),
    CONSTRAINT fk_cobranca_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id_funcionario),
    INDEX idx_cobranca_venda (id_venda),
    INDEX idx_cobranca_funcionario (id_funcionario),
    INDEX idx_cobranca_proximo_contato (proximo_contato)
) ENGINE=InnoDB;

CREATE OR REPLACE VIEW vw_saldo_cliente AS
SELECT
    c.id_cliente,
    c.nome,
    COALESCE(SUM(v.valor_total), 0) - COALESCE(SUM(p.total_pago), 0) AS saldo_devedor,
    CASE
        WHEN COALESCE(SUM(v.valor_total), 0) - COALESCE(SUM(p.total_pago), 0) > 0
        THEN 'SIM'
        ELSE 'NAO'
    END AS esta_devendo
FROM cliente c
LEFT JOIN venda_fiada v
    ON v.id_cliente = c.id_cliente
   AND v.status <> 'CANCELADA'
LEFT JOIN (
    SELECT id_venda, SUM(valor_pago) AS total_pago
    FROM pagamento
    GROUP BY id_venda
) p ON p.id_venda = v.id_venda
GROUP BY c.id_cliente, c.nome;
