package br.com.debcontrol.service;

import br.com.debcontrol.dao.RelatorioDAO;
import br.com.debcontrol.dto.ClienteResumo;
import br.com.debcontrol.dto.DashboardResumo;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.util.Formatador;

import java.io.IOException;
import java.math.BigDecimal;
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
        linhas.add("Cliente;Telefone;Saldo devedor;Atrasado");
        for (ClienteResumo item : clientesDevedores()) {
            linhas.add(csv(item.nome()) + ";" + csv(item.telefone()) + ";"
                    + Formatador.moeda(item.saldoDevedor()) + ";" + (item.possuiAtraso() ? "Sim" : "Não"));
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

    private String csv(String value) {
        if (value == null) return "";
        return value.replace(";", ",").replace("\n", " ");
    }
}
