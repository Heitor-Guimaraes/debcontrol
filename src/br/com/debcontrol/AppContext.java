package br.com.debcontrol;

import br.com.debcontrol.dao.*;
import br.com.debcontrol.service.*;

public class AppContext {
    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;
    private final FormaPagamentoService formaPagamentoService;
    private final FinanceiroService financeiroService;
    private final CobrancaService cobrancaService;
    private final RelatorioService relatorioService;

    public AppContext() {
        ClienteDAO clienteDAO = new ClienteDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        FormaPagamentoDAO formaPagamentoDAO = new FormaPagamentoDAO();
        VendaFiadaDAO vendaDAO = new VendaFiadaDAO();
        PagamentoDAO pagamentoDAO = new PagamentoDAO();
        CobrancaDAO cobrancaDAO = new CobrancaDAO();
        RelatorioDAO relatorioDAO = new RelatorioDAO();
        clienteService = new ClienteService(clienteDAO);
        funcionarioService = new FuncionarioService(funcionarioDAO);
        formaPagamentoService = new FormaPagamentoService(formaPagamentoDAO);
        financeiroService = new FinanceiroService(
                clienteDAO, funcionarioDAO, formaPagamentoDAO, vendaDAO, pagamentoDAO);
        cobrancaService = new CobrancaService(cobrancaDAO, vendaDAO, funcionarioDAO);
        relatorioService = new RelatorioService(relatorioDAO);
    }

    public ClienteService clienteService() { return clienteService; }
    public FuncionarioService funcionarioService() { return funcionarioService; }
    public FormaPagamentoService formaPagamentoService() { return formaPagamentoService; }
    public FinanceiroService financeiroService() { return financeiroService; }
    public CobrancaService cobrancaService() { return cobrancaService; }
    public RelatorioService relatorioService() { return relatorioService; }
}
