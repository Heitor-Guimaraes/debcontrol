package br.com.debcontrol.service;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.dao.*;
import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.*;
import br.com.debcontrol.util.Validador;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FinanceiroService {
    private final ClienteDAO clienteDAO;
    private final FuncionarioDAO funcionarioDAO;
    private final FormaPagamentoDAO formaPagamentoDAO;
    private final VendaFiadaDAO vendaDAO;
    private final PagamentoDAO pagamentoDAO;

    public FinanceiroService(ClienteDAO clienteDAO, FuncionarioDAO funcionarioDAO,
                             FormaPagamentoDAO formaPagamentoDAO, VendaFiadaDAO vendaDAO,
                             PagamentoDAO pagamentoDAO) {
        this.clienteDAO = clienteDAO;
        this.funcionarioDAO = funcionarioDAO;
        this.formaPagamentoDAO = formaPagamentoDAO;
        this.vendaDAO = vendaDAO;
        this.pagamentoDAO = pagamentoDAO;
    }

    public VendaFiada registrarVenda(long idCliente, long idFuncionario, BigDecimal valor,
                                     LocalDate dataVenda, LocalDate vencimento,
                                     String descricaoCompra, String observacao) {
        Validador.valorMonetario(valor);
        Cliente cliente = clienteDAO.buscarPorId(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Cliente não encontrado."));
        Funcionario funcionario = funcionarioDAO.buscarPorId(idFuncionario)
                .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));
        if (!cliente.isAtivo()) throw new RegraNegocioException("O cliente está desativado.");
        if (!funcionario.isAtivo()) throw new RegraNegocioException("O funcionário está desativado.");
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor da venda deve ser maior que zero.");
        }
        if (dataVenda == null || vencimento == null) {
            throw new RegraNegocioException("As datas da venda e do vencimento são obrigatórias.");
        }
        if (dataVenda.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data da venda não pode estar no futuro.");
        }
        if (vencimento.isBefore(dataVenda)) {
            throw new RegraNegocioException("O vencimento não pode ser anterior à data da venda.");
        }
        if (vencimento.isAfter(dataVenda.plusMonths(1))) {
            throw new RegraNegocioException("O vencimento deve ocorrer em até um mês.");
        }
        VendaFiada venda = new VendaFiada();
        venda.setIdCliente(idCliente);
        venda.setClienteNome(cliente.getNome());
        venda.setIdFuncionario(idFuncionario);
        venda.setFuncionarioNome(funcionario.getNome());
        venda.setDataVenda(dataVenda.atStartOfDay());
        venda.setDataVencimento(vencimento);
        venda.setDescricaoCompra(Validador.textoObrigatorio(descricaoCompra, "Descrição da compra", 255));
        venda.setValorTotal(valor);
        venda.setStatus(StatusVenda.ABERTA);
        venda.setObservacao(Validador.textoOpcional(observacao, "Observação", 255));
        return vendaDAO.inserir(venda);
    }

    public Pagamento registrarPagamento(long idVenda, long idFuncionario, long idFormaPagamento,
                                         BigDecimal valor, LocalDate data, String observacao) {
        Validador.valorMonetario(valor);
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O pagamento deve ser maior que zero.");
        }
        if (data == null || data.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data do pagamento não pode estar no futuro.");
        }
        Funcionario funcionario = funcionarioDAO.buscarPorId(idFuncionario)
                .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));
        FormaPagamento forma = formaPagamentoDAO.buscarPorId(idFormaPagamento)
                .orElseThrow(() -> new RegraNegocioException("Forma de pagamento não encontrada."));
        if (!funcionario.isAtivo()) throw new RegraNegocioException("O funcionário está desativado.");
        if (!forma.isAtivo()) throw new RegraNegocioException("A forma de pagamento está desativada.");

        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            try {
                VendaFiada venda = vendaDAO.buscarPorId(connection, idVenda)
                        .orElseThrow(() -> new RegraNegocioException("Venda não encontrada."));
                if (!(venda.getStatus() == StatusVenda.ABERTA
                        || venda.getStatus() == StatusVenda.PARCIAL
                        || venda.getStatus() == StatusVenda.ATRASADA)) {
                    throw new RegraNegocioException("Esta venda não pode receber pagamentos.");
                }
                if (data.isBefore(venda.getDataVenda().toLocalDate())) {
                    throw new RegraNegocioException("O pagamento não pode ser anterior à venda.");
                }
                if (valor.compareTo(venda.getSaldo()) > 0) {
                    throw new RegraNegocioException("O pagamento não pode ultrapassar o saldo da venda.");
                }
                Pagamento pagamento = new Pagamento();
                pagamento.setIdVenda(idVenda);
                pagamento.setIdCliente(venda.getIdCliente());
                pagamento.setClienteNome(venda.getClienteNome());
                pagamento.setIdFuncionario(idFuncionario);
                pagamento.setFuncionarioNome(funcionario.getNome());
                pagamento.setIdFormaPagamento(idFormaPagamento);
                pagamento.setFormaPagamentoNome(forma.getNome());
                pagamento.setDataPagamento(data.atStartOfDay());
                pagamento.setValorPago(valor);
                pagamento.setObservacao(Validador.textoOpcional(observacao, "Observação", 255));
                pagamentoDAO.inserir(connection, pagamento);

                StatusVenda novoStatus;
                if (valor.compareTo(venda.getSaldo()) == 0) novoStatus = StatusVenda.QUITADA;
                else if (venda.isAtrasada()) novoStatus = StatusVenda.ATRASADA;
                else novoStatus = StatusVenda.PARCIAL;
                vendaDAO.atualizarStatus(connection, idVenda, novoStatus);
                connection.commit();
                return pagamento;
            } catch (RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao concluir a transação de pagamento.", exception);
        }
    }

    public void cancelarVenda(long idVenda, String justificativa) {
        VendaFiada venda = buscarVenda(idVenda);
        if (!(venda.getStatus() == StatusVenda.ABERTA || venda.getStatus() == StatusVenda.ATRASADA)) {
            throw new RegraNegocioException("Somente vendas sem pagamento podem ser canceladas.");
        }
        if (venda.getValorPago().compareTo(BigDecimal.ZERO) > 0) {
            throw new RegraNegocioException("Uma venda com pagamentos registrados não pode ser cancelada.");
        }
        vendaDAO.cancelar(idVenda, Validador.textoObrigatorio(justificativa, "Justificativa", 255));
    }

    public VendaFiada buscarVenda(long id) {
        return vendaDAO.buscarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Venda não encontrada."));
    }

    public List<VendaFiada> listarVendas(String filtro, boolean apenasPendentes) {
        return vendaDAO.listar(filtro, apenasPendentes);
    }

    public List<VendaFiada> extratoCliente(long idCliente) {
        return vendaDAO.listarPorCliente(idCliente);
    }

    public List<Pagamento> listarPagamentos(String filtro) {
        return pagamentoDAO.listar(filtro);
    }

    public BigDecimal saldoCliente(long idCliente) {
        return vendaDAO.calcularSaldoCliente(idCliente);
    }
}
