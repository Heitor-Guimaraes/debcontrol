package br.com.debcontrol.service;

import br.com.debcontrol.dao.RelatorioDAO;
import br.com.debcontrol.dto.ClienteResumo;
import br.com.debcontrol.dto.DashboardResumo;
import br.com.debcontrol.exception.RegraNegocioException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioService {
    private final RelatorioDAO relatorioDAO;

    public RelatorioService(RelatorioDAO relatorioDAO) {
        this.relatorioDAO = relatorioDAO;
    }

    public DashboardResumo dashboard() {
        return relatorioDAO.carregarDashboard();
    }

    public List<ClienteResumo> clientesDevedores() {
        return relatorioDAO.listarClientesDevedores();
    }

    public BigDecimal totalRecebido(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new RegraNegocioException("Informe um período válido.");
        }
        return relatorioDAO.totalRecebido(inicio, fim);
    }

    public void exportarClientesDevedores(Path arquivo) {
        List<String> linhas = new ArrayList<>();
        // A marca BOM faz o Excel reconhecer UTF-8 e exibir os acentos corretamente.
        linhas.add("\uFEFFCliente;Telefone;Saldo devedor (R$);Atrasado");
        for (ClienteResumo item : clientesDevedores()) {
            linhas.add(csv(item.nome()) + ";" + csv(item.telefone()) + ";"
                    + decimal(item.saldoDevedor()) + ";" + (item.possuiAtraso() ? "Sim" : "Não"));
        }
        try {
            Path parent = arquivo.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(arquivo, linhas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            throw new RegraNegocioException("Não foi possível exportar o relatório: " + exception.getMessage());
        }
    }

    /** Valor numérico sem símbolo de moeda, para que a planilha consiga somar a coluna. */
    private String decimal(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',');
    }

    private String csv(String value) {
        if (value == null) return "";
        // Texto iniciado por estes caracteres seria interpretado como fórmula pela planilha.
        String text = value.matches("(?s)^[=+\\-@\t\r].*") ? "'" + value : value;
        if (text.contains(";") || text.contains("\"") || text.contains("\n") || text.contains("\r")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
