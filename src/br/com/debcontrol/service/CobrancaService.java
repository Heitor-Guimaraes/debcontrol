package br.com.debcontrol.service;

import br.com.debcontrol.dao.CobrancaDAO;
import br.com.debcontrol.dao.FuncionarioDAO;
import br.com.debcontrol.dao.VendaFiadaDAO;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.*;
import br.com.debcontrol.util.Validador;

import java.time.LocalDate;
import java.util.List;

public class CobrancaService {
    private final CobrancaDAO cobrancaDAO;
    private final VendaFiadaDAO vendaDAO;
    private final FuncionarioDAO funcionarioDAO;

    public CobrancaService(CobrancaDAO cobrancaDAO, VendaFiadaDAO vendaDAO, FuncionarioDAO funcionarioDAO) {
        this.cobrancaDAO = cobrancaDAO;
        this.vendaDAO = vendaDAO;
        this.funcionarioDAO = funcionarioDAO;
    }

    public Cobranca registrar(long idVenda, long idFuncionario, LocalDate data,
                              MeioContato meioContato, String resultado,
                              LocalDate proximoContato, String observacao) {
        VendaFiada venda = vendaDAO.buscarPorId(idVenda)
                .orElseThrow(() -> new RegraNegocioException("Venda não encontrada."));
        Funcionario funcionario = funcionarioDAO.buscarPorId(idFuncionario)
                .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));
        if (venda.getStatus() == StatusVenda.QUITADA || venda.getStatus() == StatusVenda.CANCELADA) {
            throw new RegraNegocioException("Não é possível cobrar uma venda quitada ou cancelada.");
        }
        if (!funcionario.isAtivo()) throw new RegraNegocioException("O funcionário está desativado.");
        if (data == null || data.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("Informe uma data de cobrança válida.");
        }
        if (data.isBefore(venda.getDataVenda().toLocalDate())) {
            throw new RegraNegocioException("A cobrança não pode ser anterior à venda.");
        }
        if (meioContato == null) throw new RegraNegocioException("Informe o meio de contato.");
        if (proximoContato != null && proximoContato.isBefore(data)) {
            throw new RegraNegocioException("O próximo contato não pode ser anterior à cobrança.");
        }
        Cobranca cobranca = new Cobranca();
        cobranca.setIdVenda(idVenda);
        cobranca.setIdFuncionario(idFuncionario);
        cobranca.setClienteNome(venda.getClienteNome());
        cobranca.setFuncionarioNome(funcionario.getNome());
        cobranca.setDataCobranca(data.atStartOfDay());
        cobranca.setMeioContato(meioContato);
        cobranca.setResultado(Validador.textoObrigatorio(resultado, "Resultado", 150));
        cobranca.setProximoContato(proximoContato);
        cobranca.setObservacao(Validador.textoOpcional(observacao, "Observação", 255));
        return cobrancaDAO.inserir(cobranca);
    }

    public List<Cobranca> listar(String filtro) {
        return cobrancaDAO.listar(filtro);
    }
}
