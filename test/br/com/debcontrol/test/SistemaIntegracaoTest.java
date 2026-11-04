package br.com.debcontrol.test;

import br.com.debcontrol.AppContext;
import br.com.debcontrol.config.*;
import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.*;
import br.com.debcontrol.ui.panels.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

/** Integração em um banco temporário exclusivo, criado no mesmo servidor configurado. */
public final class SistemaIntegracaoTest {
    private static int checks;
    private static final LocalDate HOJE = LocalDate.now();

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("--isolado")) { executar(); return; }
        String database = "debcontrol_teste_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = ConnectionFactory.getConnection(); Statement stmt = connection.createStatement()) {
            System.out.println("Servidor: " + connection.getMetaData().getDatabaseProductName() + " "
                    + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Java: " + System.getProperty("java.version"));
            System.out.println("Conexao com o banco configurado: OK. Testes em schema temporario: " + database);
            // O nome é gerado internamente; nunca se apaga o banco configurado do usuário.
            stmt.executeUpdate("CREATE DATABASE `" + database + "`");
            try {
                for (String sql : Files.readString(Path.of("sql/banco_mercado_fiado.sql"))
                        .replace("mercado_fiado", database).split(";")) {
                    if (!sql.isBlank()) stmt.execute(sql);
                }
                DatabaseConfig config = DatabaseConfig.load();
                String url = config.url();
                int slash = url.indexOf('/', "jdbc:mysql://".length());
                if (slash < 0) throw new IllegalStateException("URL JDBC sem banco.");
                int query = url.indexOf('?', slash);
                String testUrl = url.substring(0, slash + 1) + database + (query < 0 ? "" : url.substring(query));
                ProcessBuilder process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                        "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Djava.awt.headless=" + !(args.length > 0 && args[0].equals("--gui")), "-cp",
                        System.getProperty("java.class.path"),
                        args.length > 0 && args[0].equals("--gui") ? "br.com.debcontrol.test.InterfaceIntegracaoTest" : SistemaIntegracaoTest.class.getName(), "--isolado");
                process.environment().put("FIADO_DB_URL", testUrl);
                process.inheritIO();
                int result = process.start().waitFor();
                if (result != 0) throw new AssertionError("Teste de integracao falhou: " + result);
            } finally {
                stmt.executeUpdate("DROP DATABASE `" + database + "`");
                System.out.println("Banco temporario removido. Dados do banco original preservados.");
            }
        }
    }

    private static void executar() throws Exception {
        AppContext app = new AppContext();
        var clientes = app.clienteService();
        var funcionarios = app.funcionarioService();
        var formas = app.formaPagamentoService();
        var financeiro = app.financeiroService();
        var cobrancas = app.cobrancaService();
        var relatorios = app.relatorioService();
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Integracao"); cliente.setCpf("52998224725"); cliente.setTelefone("11999999999");
        clientes.salvar(cliente);
        ok(clientes.buscar(cliente.getIdCliente()).getNome().equals(cliente.getNome()), "Cadastro e consulta de cliente");
        cliente.setNome("Cliente Integracao Alterado"); clientes.salvar(cliente);
        ok(clientes.listar("Alterado", true).size() == 1, "Alteracao e filtro de cliente");
        Cliente duplicado = new Cliente(); duplicado.setNome("Duplicado"); duplicado.setCpf(cliente.getCpf()); duplicado.setTelefone(cliente.getTelefone());
        rejeita(() -> clientes.salvar(duplicado), "Bloqueio de CPF duplicado");
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Operador Integracao"); funcionario.setCargo("Caixa"); funcionario.setLogin("teste_integracao");
        funcionarios.salvar(funcionario, "SenhaTeste123");
        ok(funcionarios.buscar(funcionario.getIdFuncionario()).getSenhaHash().length() == 64, "Funcionario persistido com hash de senha");
        funcionario.setNome("Operador Alterado"); funcionarios.salvar(funcionario, null);
        ok(funcionarios.listar("Alterado", true).size() == 1, "Alteracao e filtro de funcionario");
        FormaPagamento forma = new FormaPagamento(); forma.setNome("Teste dinheiro"); forma.setDescricao("Teste"); formas.salvar(forma);
        forma.setDescricao("Descricao alterada"); formas.salvar(forma);
        ok(formas.buscar(forma.getIdFormaPagamento()).getDescricao().equals("Descricao alterada"), "Cadastro, consulta e alteracao de forma de pagamento");
        long c = cliente.getIdCliente(), f = funcionario.getIdFuncionario(), p = forma.getIdFormaPagamento();
        var venda = financeiro.registrarVenda(c, f, valor("100"), HOJE, HOJE.plusDays(7), "Venda teste", null);
        long v = venda.getIdVenda();
        ok(financeiro.buscarVenda(v).getStatus() == StatusVenda.ABERTA, "Venda persistida como ABERTA");
        moeda("100", financeiro.saldoCliente(c), "Saldo inicial do cliente");
        rejeita(() -> financeiro.registrarVenda(c, f, valor("10"), HOJE.plusDays(1), HOJE.plusDays(2), "Futura", null), "Bloqueio de venda futura");
        rejeita(() -> financeiro.registrarVenda(c, f, valor("10"), HOJE, HOJE.plusMonths(1).plusDays(1), "Prazo", null), "Bloqueio de vencimento acima de um mes");
        rejeita(() -> financeiro.registrarPagamento(v, f, p, valor("10"), HOJE.minusDays(1), null), "Bloqueio de pagamento anterior a venda");
        financeiro.registrarPagamento(v, f, p, valor("40"), HOJE, "Parcial");
        ok(financeiro.buscarVenda(v).getStatus() == StatusVenda.PARCIAL, "Pagamento muda status para PARCIAL");
        moeda("60", financeiro.buscarVenda(v).getSaldo(), "Saldo parcial persistido");
        rejeita(() -> financeiro.registrarPagamento(v, f, p, valor("61"), HOJE, null), "Bloqueio de pagamento acima do saldo");
        ok(financeiro.listarPagamentos("Integracao").size() == 1, "Pagamento rejeitado nao foi gravado");
        rejeita(() -> financeiro.cancelarVenda(v, "Teste"), "Bloqueio de cancelamento com pagamento");
        financeiro.registrarPagamento(v, f, p, valor("60"), HOJE, "Quitacao");
        ok(financeiro.buscarVenda(v).getStatus() == StatusVenda.QUITADA, "Quitacao muda status para QUITADA");
        moeda("0", financeiro.saldoCliente(c), "Saldo zerado apos quitacao");
        rejeita(() -> financeiro.registrarPagamento(v, f, p, valor("1"), HOJE, null), "Bloqueio de pagamento de venda quitada");
        rejeita(() -> cobrancas.registrar(v, f, HOJE, MeioContato.WHATSAPP, "Teste", null, null), "Bloqueio de cobranca de venda quitada");
        var pendente = financeiro.registrarVenda(c, f, valor("50"), HOJE.minusDays(2), HOJE.minusDays(1), "Venda atrasada", null);
        long a = pendente.getIdVenda();
        ok(financeiro.listarVendas("Integracao", true).size() == 1, "Filtro de vendas pendentes");
        ok(financeiro.buscarVenda(a).getStatus() == StatusVenda.ATRASADA, "Atualizacao de venda atrasada");
        cobrancas.registrar(a, f, HOJE, MeioContato.WHATSAPP, "Cliente avisado", HOJE.plusDays(1), "Teste");
        ok(cobrancas.listar("Integracao").size() == 1, "Cobranca gravada e consultada");
        rejeita(() -> cobrancas.registrar(a, f, HOJE, MeioContato.TELEFONE, "Teste", HOJE.minusDays(1), null), "Bloqueio de proximo contato anterior a cobranca");
        moeda("50", relatorios.dashboard().totalAReceber(), "Dashboard total a receber");
        moeda("100", relatorios.dashboard().totalRecebidoMes(), "Dashboard recebido no mes");
        ok(relatorios.dashboard().clientesDevedores() == 1 && relatorios.dashboard().dividasAtrasadas() == 1, "Dashboard devedores e atrasos");
        ok(relatorios.clientesDevedores().size() == 1 && relatorios.clientesDevedores().getFirst().possuiAtraso(), "Relatorio de devedores e atraso");
        moeda("100", relatorios.totalRecebido(HOJE, HOJE), "Recebimentos no periodo");
        rejeita(() -> relatorios.totalRecebido(HOJE, HOJE.minusDays(1)), "Bloqueio de periodo invalido");
        Path csv = Path.of("docs/aula12/teste-devedores.csv");
        relatorios.exportarClientesDevedores(csv);
        ok(Files.readAllLines(csv).size() == 2 && Files.readString(csv).contains(cliente.getNome()), "CSV exportado com cabecalho e cliente correto");
        ok(financeiro.extratoCliente(c).size() == 2, "Extrato do cliente com as duas vendas");
        rejeita(() -> clientes.excluir(c), "Exclusao de cliente com historico bloqueada");
        rejeita(() -> funcionarios.excluir(f), "Exclusao de funcionario com historico bloqueada");
        rejeita(() -> formas.excluir(p), "Exclusao de forma utilizada bloqueada");
        var cancelar = financeiro.registrarVenda(c, f, valor("25"), HOJE, HOJE, "Cancelar", null);
        financeiro.cancelarVenda(cancelar.getIdVenda(), "Teste de cancelamento");
        ok(financeiro.buscarVenda(cancelar.getIdVenda()).getStatus() == StatusVenda.CANCELADA, "Cancelamento de venda sem pagamento");
        moeda("50", financeiro.saldoCliente(c), "Venda cancelada nao aumenta saldo");
        var concorrente = financeiro.registrarVenda(c, f, valor("100"), HOJE, HOJE, "Concorrencia", null);
        CountDownLatch inicio = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> pagar = () -> {
                inicio.await();
                try { financeiro.registrarPagamento(concorrente.getIdVenda(), f, p, valor("70"), HOJE, "Concorrente"); return true; }
                catch (RegraNegocioException expected) { return false; }
            };
            Future<Boolean> primeiro = executor.submit(pagar), segundo = executor.submit(pagar);
            inicio.countDown();
            ok(primeiro.get(15, TimeUnit.SECONDS) != segundo.get(15, TimeUnit.SECONDS), "Dois pagamentos simultaneos: apenas um aprovado");
        }
        moeda("30", financeiro.buscarVenda(concorrente.getIdVenda()).getSaldo(), "Concorrencia preserva saldo sem pagamento excedente");
        testarPaineis(app);
        clientes.desativar(c);
        rejeita(() -> financeiro.registrarVenda(c, f, valor("10"), HOJE, HOJE, "Inativo", null), "Bloqueio de venda para cliente inativo");
        funcionarios.desativar(f);
        rejeita(() -> financeiro.registrarPagamento(a, f, p, valor("1"), HOJE, null), "Bloqueio de funcionario inativo");
        formas.desativar(p);
        ok(!formas.buscar(p).isAtivo() && !funcionarios.buscar(f).isAtivo() && !clientes.buscar(c).isAtivo(), "Desativacoes persistidas nos tres cadastros");
        System.out.println("APROVADOS: " + checks + " testes de integracao real. Interface: carregamento e pintura dos paineis; sem cliques manuais nos formularios.");
    }

    private static void testarPaineis(AppContext a) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            br.com.debcontrol.ui.UiTheme.install();
            JPanel[] paineis = {
                new DashboardPanel(a.relatorioService()),
                new ClientesPanel(a.clienteService(), a.financeiroService(), () -> {}),
                new FuncionariosPanel(a.funcionarioService(), () -> {}),
                new FormasPagamentoPanel(a.formaPagamentoService(), () -> {}),
                new VendasPanel(a.clienteService(), a.funcionarioService(), a.financeiroService(), () -> {}),
                new PagamentosPanel(a.financeiroService(), a.funcionarioService(), a.formaPagamentoService(), () -> {}),
                new CobrancasPanel(a.cobrancaService(), a.financeiroService(), a.funcionarioService(), () -> {}),
                new RelatoriosPanel(a.relatorioService())
            };
            for (JPanel painel : paineis) {
                try {
                    painel.getClass().getMethod("atualizar").invoke(painel);
                    painel.setSize(1280, 760); layout(painel);
                    var image = new java.awt.image.BufferedImage(1280, 760, java.awt.image.BufferedImage.TYPE_INT_RGB);
                    var graphics = image.createGraphics();
                    try { painel.printAll(graphics); } finally { graphics.dispose(); }
                    Path folder = Path.of("docs/aula12/telas-validacao"); Files.createDirectories(folder);
                    javax.imageio.ImageIO.write(image, "png", folder.resolve(painel.getClass().getSimpleName() + ".png").toFile());
                    ok(true, painel.getClass().getSimpleName() + " carrega banco real e renderiza");
                } catch (Exception e) { throw new AssertionError("Falha no painel " + painel.getClass().getSimpleName(), e); }
            }
        });
    }
    private static void layout(java.awt.Container container) {
        container.doLayout();
        for (var child : container.getComponents()) if (child instanceof java.awt.Container c) layout(c);
    }
    private static BigDecimal valor(String v) { return new BigDecimal(v); }
    private static void moeda(String esperado, BigDecimal atual, String nome) { ok(valor(esperado).compareTo(atual) == 0, nome + " = " + atual); }
    private static void rejeita(Runnable operacao, String nome) {
        try { operacao.run(); } catch (RegraNegocioException expected) { ok(true, nome); return; }
        throw new AssertionError(nome + ": operacao deveria ser rejeitada");
    }
    private static void ok(boolean condicao, String nome) {
        if (!condicao) throw new AssertionError(nome);
        checks++; System.out.println("[OK] " + nome);
    }
}
