package br.com.debcontrol.test;

import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.StatusVenda;
import br.com.debcontrol.model.VendaFiada;
import br.com.debcontrol.util.Formatador;
import br.com.debcontrol.util.SenhaUtil;
import br.com.debcontrol.util.Validador;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestRunner {
    private static int executados;

    public static void main(String[] args) {
        testar("conversão de moeda brasileira", () -> igual(new BigDecimal("1234.56"),
                Formatador.parseMoeda("R$ 1.234,56", "Valor")));
        testar("normalização de CPF", () -> igual("12345678901",
                Validador.somenteNumerosOuNull("123.456.789-01")));
        testar("campo obrigatório", () -> {
            try {
                Validador.textoObrigatorio("   ", "Nome");
                throw new AssertionError("A validação deveria ter falhado.");
            } catch (RegraNegocioException expected) {
                // comportamento esperado
            }
        });
        testar("identificação de dívida atrasada", () -> {
            VendaFiada venda = new VendaFiada();
            venda.setStatus(StatusVenda.ABERTA);
            venda.setSaldo(new BigDecimal("10.00"));
            venda.setDataVencimento(LocalDate.now().minusDays(1));
            verdadeiro(venda.isAtrasada());
        });
        testar("venda quitada não fica atrasada", () -> {
            VendaFiada venda = new VendaFiada();
            venda.setStatus(StatusVenda.QUITADA);
            venda.setSaldo(BigDecimal.ZERO);
            venda.setDataVencimento(LocalDate.now().minusDays(1));
            falso(venda.isAtrasada());
        });
        testar("venda parcial vencida fica atrasada", () -> {
            VendaFiada venda = new VendaFiada();
            venda.setStatus(StatusVenda.PARCIAL);
            venda.setSaldo(new BigDecimal("1.00"));
            venda.setDataVencimento(LocalDate.now().minusDays(1));
            verdadeiro(venda.isAtrasada());
        });
        testar("hash de senha usa PBKDF2 com sal e é verificável", () -> {
            String primeiro = SenhaUtil.hash("Senha123");
            verdadeiro(primeiro.startsWith("pbkdf2-sha256$"));
            falso(primeiro.equals(SenhaUtil.hash("Senha123")));
            verdadeiro(SenhaUtil.verificar("Senha123", primeiro));
            falso(SenhaUtil.verificar("Senha124", primeiro));
        });
        testar("hash SHA-256 antigo continua verificável", () -> {
            String antigo = "c00357563669ed21c34e13687cad669038eb88a2831fc8109b40ddc62f63e934";
            verdadeiro(SenhaUtil.verificar("Senha123", antigo));
            falso(SenhaUtil.verificar("Senha456", antigo));
            falso(SenhaUtil.verificar("Senha123", "formato-desconhecido"));
        });
        testar("senha curta é rejeitada", () -> rejeita(() -> SenhaUtil.hash("1234567")));
        testar("CPF com dígitos corretos é aceito", () -> verdadeiro(Validador.cpfValido("52998224725")));
        testar("CPF inválido é rejeitado", () -> {
            falso(Validador.cpfValido("52998224724"));
            falso(Validador.cpfValido("11111111111"));
            falso(Validador.cpfValido("123"));
            falso(Validador.cpfValido(null));
        });
        testar("data impossível é rejeitada", () -> rejeita(() -> Formatador.parseData("31/02/2026", "Data")));
        testar("data nula é rejeitada", () -> rejeita(() -> Formatador.parseData(null, "Data")));
        testar("data de ano bissexto é aceita", () -> igual(LocalDate.of(2024, 2, 29),
                Formatador.parseData("29/02/2024", "Data")));
        testar("ponto decimal ambíguo é rejeitado", () -> rejeita(() -> Formatador.parseMoeda("10.50", "Valor")));
        testar("centavos excedentes são rejeitados", () -> rejeita(() -> Formatador.parseMoeda("10,123", "Valor")));
        testar("agrupamento inválido é rejeitado", () -> rejeita(() -> Formatador.parseMoeda("12.34,56", "Valor")));
        testar("valor inteiro é aceito", () -> igual(new BigDecimal("150"), Formatador.parseMoeda("150", "Valor")));
        testar("valor vazio é rejeitado", () -> rejeita(() -> Formatador.parseMoeda(" ", "Valor")));
        testar("venda cancelada não é atrasada", () -> {
            VendaFiada venda = new VendaFiada();
            venda.setStatus(StatusVenda.CANCELADA);
            venda.setSaldo(new BigDecimal("10.00"));
            venda.setDataVencimento(LocalDate.now().minusDays(1));
            falso(venda.isAtrasada());
        });
        testar("oito painéis Swing são construídos sem banco", () -> {
            try {
                javax.swing.SwingUtilities.invokeAndWait(() -> {
                    br.com.debcontrol.ui.UiTheme.install();
                    javax.swing.JPanel[] panels = {
                        new br.com.debcontrol.ui.panels.DashboardPanel(),
                        new br.com.debcontrol.ui.panels.ClientesPanel(),
                        new br.com.debcontrol.ui.panels.FuncionariosPanel(),
                        new br.com.debcontrol.ui.panels.FormasPagamentoPanel(),
                        new br.com.debcontrol.ui.panels.VendasPanel(),
                        new br.com.debcontrol.ui.panels.PagamentosPanel(),
                        new br.com.debcontrol.ui.panels.CobrancasPanel(),
                        new br.com.debcontrol.ui.panels.RelatoriosPanel()
                    };
                    for (javax.swing.JPanel panel : panels) {
                        verdadeiro(panel.getComponentCount() > 0);
                        panel.setSize(1100, 700);
                        panel.doLayout();
                        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                                1100, 700, java.awt.image.BufferedImage.TYPE_INT_RGB);
                        java.awt.Graphics2D graphics = image.createGraphics();
                        try { panel.printAll(graphics); } finally { graphics.dispose(); }
                    }
                });
            } catch (Exception exception) {
                throw new AssertionError("Falha ao construir os painéis Swing", exception);
            }
        });
        testar("tabela mantém contraste ao selecionar", () -> {
            javax.swing.JTable table = new javax.swing.JTable(new Object[][]{{"Ana"}, {"Bruno"}}, new Object[]{"Cliente"});
            br.com.debcontrol.ui.UiTheme.styleTable(table);
            var renderer = table.getCellRenderer(0, 0);
            var first = renderer.getTableCellRendererComponent(table, "Ana", false, false, 0, 0);
            java.awt.Color even = first.getBackground();
            var second = renderer.getTableCellRendererComponent(table, "Bruno", false, false, 1, 0);
            falso(even.equals(second.getBackground()));
            var selected = renderer.getTableCellRendererComponent(table, "Ana", true, true, 0, 0);
            igual(table.getSelectionBackground(), selected.getBackground());
            igual(table.getSelectionForeground(), selected.getForeground());
            falso(table.getTableHeader().getReorderingAllowed());
        });
        testar("botão principal preserva foco de teclado", () -> {
            javax.swing.JButton button = new javax.swing.JButton("Salvar");
            br.com.debcontrol.ui.UiTheme.stylePrimaryButton(button);
            verdadeiro(button.isFocusable());
            verdadeiro(button.isFocusPainted());
        });
        testar("janela cabe em notebook, desktop e área com escala", () -> {
            java.awt.Rectangle[] screens = {
                new java.awt.Rectangle(0, 0, 1366, 728),
                new java.awt.Rectangle(0, 0, 1920, 1040),
                new java.awt.Rectangle(0, 0, 1093, 574),
                new java.awt.Rectangle(-1920, 0, 1920, 1040)
            };
            for (var screen : screens) {
                var bounds = br.com.debcontrol.ui.WindowSizing.fitBounds(screen, new java.awt.Dimension(1240, 760));
                verdadeiro(screen.contains(bounds));
                verdadeiro(bounds.width > 0 && bounds.height > 0);
            }
        });
        testar("rolagem mantém conteúdo acessível em viewport pequeno", () -> {
            var panel = new br.com.debcontrol.ui.components.ScrollablePanel(new javax.swing.JPanel(), 900, 560);
            var viewport = new javax.swing.JViewport();
            viewport.setView(panel);
            viewport.setSize(700, 400);
            falso(panel.getScrollableTracksViewportWidth());
            falso(panel.getScrollableTracksViewportHeight());
            viewport.setSize(1200, 800);
            verdadeiro(panel.getScrollableTracksViewportWidth());
            verdadeiro(panel.getScrollableTracksViewportHeight());
        });
        testar("CRUD e CPF duplicado na camada service com DAO em memória", Aula11Tests::crudService);
        testar("exclusão bloqueada quando DAO informa histórico", Aula11Tests::exclusaoProtegida);
        testar("telefone sem números é rejeitado", Aula11Tests::telefoneInvalido);
        testar("CPF inválido é bloqueado na camada service", Aula11Tests::cpfInvalido);
        testar("forma de pagamento com nome repetido é bloqueada", Aula11Tests::formaPagamentoDuplicada);
        testar("CSV protege separador, aspas, fórmulas e acentos", Aula11Tests::csvSeguro);
        testar("campo acima do limite é rejeitado", () -> rejeita(() -> Validador.textoObrigatorio("A".repeat(121), "Nome", 120)));
        testar("valor com fração de centavo é rejeitado", () -> rejeita(() -> Validador.valorMonetario(new BigDecimal("0.001"))));
        testar("valor acima do DECIMAL é rejeitado", () -> rejeita(() -> Validador.valorMonetario(new BigDecimal("100000000.00"))));
        testar("limite monetário e centavos são aceitos", () -> {
            Validador.valorMonetario(new BigDecimal("0.01"));
            Validador.valorMonetario(new BigDecimal("99999999.99"));
        });
        System.out.println("Todos os " + executados + " testes passaram.");
    }

    private static void rejeita(Runnable action) {
        try {
            action.run();
        } catch (RegraNegocioException expected) {
            return;
        }
        throw new AssertionError("A entrada inválida deveria ser rejeitada.");
    }

    private static void testar(String name, Runnable test) {
        test.run();
        executados++;
        System.out.println("[OK] " + name);
    }

    private static void igual(Object expected, Object actual) {
        if (!expected.equals(actual)) throw new AssertionError("Esperado " + expected + ", obtido " + actual);
    }

    private static void verdadeiro(boolean value) {
        if (!value) throw new AssertionError("O valor deveria ser verdadeiro.");
    }

    private static void falso(boolean value) {
        if (value) throw new AssertionError("O valor deveria ser falso.");
    }
}
