package br.com.debcontrol.ui;

import br.com.debcontrol.AppContext;
import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.ScrollablePanel;
import br.com.debcontrol.ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final AppContext context;
    private final CardLayout cardLayout = new CardLayout();
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private DashboardPanel dashboardPanel;
    private ClientesPanel clientesPanel;
    private FuncionariosPanel funcionariosPanel;
    private FormasPagamentoPanel formasPagamentoPanel;
    private VendasPanel vendasPanel;
    private PagamentosPanel pagamentosPanel;
    private CobrancasPanel cobrancasPanel;
    private RelatoriosPanel relatoriosPanel;

    /** Construtor sem parâmetros usado pelo GUI Builder do Apache NetBeans. */
    public MainFrame() {
        this.context = null;
        initComponents();
        configurar();
    }

    public MainFrame(AppContext context) {
        this.context = context;
        initComponents();
        configurar();
        criarTelas();
        navegar("DASHBOARD");
    }

    private void configurar() {
        setTitle("debcontrol");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        WindowSizing.fit(this, new Dimension(1240, 760), new Dimension(800, 480));
        sidebarPanel.setBackground(UiTheme.SIDEBAR);
        brandLabel.setForeground(Color.WHITE);
        brandSubtitle.setForeground(new Color(148, 163, 184));
        navPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(null);
        UiTheme.styleSecondaryButton(connectionButton);
        connectionButton.setToolTipText("Verifica a conexão com o banco de dados");
        connectionButton.addActionListener(event -> testarConexao());
        JScrollPane navigationScroll = scrollArea(sidebarPanel, 220, 640);
        navigationScroll.setPreferredSize(new Dimension(250, 0));
        add(navigationScroll, BorderLayout.WEST);
    }

    private void testarConexao() {
        try {
            MessageBox.success(this, ConnectionFactory.descreverConexao());
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }

    private JScrollPane scrollArea(JComponent content, int width, int height) {
        JScrollPane scroll = new JScrollPane(new ScrollablePanel(content, width, height));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.getHorizontalScrollBar().setUnitIncrement(24);
        scroll.getViewport().setBackground(UiTheme.BACKGROUND);
        return scroll;
    }

    private void criarTelas() {
        Runnable refreshAll = this::atualizarTelas;
        dashboardPanel = new DashboardPanel(context.relatorioService());
        clientesPanel = new ClientesPanel(context.clienteService(), context.financeiroService(), refreshAll);
        funcionariosPanel = new FuncionariosPanel(context.funcionarioService(), refreshAll);
        formasPagamentoPanel = new FormasPagamentoPanel(context.formaPagamentoService(), refreshAll);
        vendasPanel = new VendasPanel(
                context.clienteService(), context.funcionarioService(), context.financeiroService(), refreshAll);
        pagamentosPanel = new PagamentosPanel(
                context.financeiroService(), context.funcionarioService(), context.formaPagamentoService(), refreshAll);
        cobrancasPanel = new CobrancasPanel(
                context.cobrancaService(), context.financeiroService(), context.funcionarioService(), refreshAll);
        relatoriosPanel = new RelatoriosPanel(context.relatorioService());
        contentPanel.setLayout(cardLayout);
        contentPanel.add(scrollArea(dashboardPanel, 900, 560), "DASHBOARD");
        contentPanel.add(scrollArea(clientesPanel, 900, 560), "CLIENTES");
        contentPanel.add(scrollArea(funcionariosPanel, 900, 560), "FUNCIONARIOS");
        contentPanel.add(scrollArea(formasPagamentoPanel, 900, 560), "FORMAS");
        contentPanel.add(scrollArea(vendasPanel, 900, 560), "VENDAS");
        contentPanel.add(scrollArea(pagamentosPanel, 900, 560), "PAGAMENTOS");
        contentPanel.add(scrollArea(cobrancasPanel, 900, 560), "COBRANCAS");
        contentPanel.add(scrollArea(relatoriosPanel, 900, 560), "RELATORIOS");
        adicionarNavegacao(dashboardButton, "DASHBOARD");
        adicionarNavegacao(clientsButton, "CLIENTES");
        adicionarNavegacao(employeesButton, "FUNCIONARIOS");
        adicionarNavegacao(paymentMethodsButton, "FORMAS");
        adicionarNavegacao(salesButton, "VENDAS");
        adicionarNavegacao(paymentsButton, "PAGAMENTOS");
        adicionarNavegacao(collectionsButton, "COBRANCAS");
        adicionarNavegacao(reportsButton, "RELATORIOS");
    }

    private void adicionarNavegacao(JButton button, String card) {
        navButtons.put(card, button);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setFocusPainted(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(event -> navegar(card));
    }

    private void navegar(String card) {
        cardLayout.show(contentPanel, card);
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean selected = entry.getKey().equals(card);
            entry.getValue().setBackground(selected ? UiTheme.PRIMARY : UiTheme.SIDEBAR);
            entry.getValue().setForeground(selected ? Color.WHITE : new Color(203, 213, 225));
        }
        try {
            switch (card) {
                case "DASHBOARD" -> dashboardPanel.atualizar();
                case "CLIENTES" -> clientesPanel.atualizar();
                case "FUNCIONARIOS" -> funcionariosPanel.atualizar();
                case "FORMAS" -> formasPagamentoPanel.atualizar();
                case "VENDAS" -> vendasPanel.atualizar();
                case "PAGAMENTOS" -> pagamentosPanel.atualizar();
                case "COBRANCAS" -> cobrancasPanel.atualizar();
                case "RELATORIOS" -> relatoriosPanel.atualizar();
            }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void atualizarTelas() {
        dashboardPanel.atualizar();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        sidebarPanel = new JPanel(); brandPanel = new JPanel(); brandLabel = new JLabel("debcontrol");
        brandSubtitle = new JLabel("Gestão simples e segura"); navPanel = new JPanel();
        dashboardButton = new JButton("Visão geral"); clientsButton = new JButton("Clientes");
        employeesButton = new JButton("Funcionários"); paymentMethodsButton = new JButton("Formas de pagamento");
        salesButton = new JButton("Vendas fiadas"); paymentsButton = new JButton("Pagamentos");
        collectionsButton = new JButton("Cobranças");
        reportsButton = new JButton("Relatórios"); connectionButton = new JButton("Testar conexão"); contentPanel = new JPanel();

        setLayout(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(new EmptyBorder(26, 16, 20, 16));
        sidebarPanel.setLayout(new BorderLayout(0, 30));
        brandPanel.setOpaque(false); brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 21)); brandPanel.add(brandLabel); brandPanel.add(brandSubtitle);
        sidebarPanel.add(brandPanel, BorderLayout.NORTH);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.add(dashboardButton); navPanel.add(Box.createVerticalStrut(6)); navPanel.add(clientsButton);
        navPanel.add(Box.createVerticalStrut(6)); navPanel.add(employeesButton);
        navPanel.add(Box.createVerticalStrut(6)); navPanel.add(paymentMethodsButton);
        navPanel.add(Box.createVerticalStrut(6)); navPanel.add(salesButton);
        navPanel.add(Box.createVerticalStrut(6)); navPanel.add(paymentsButton);
        navPanel.add(Box.createVerticalStrut(6)); navPanel.add(collectionsButton);
        navPanel.add(Box.createVerticalStrut(6)); navPanel.add(reportsButton);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
        sidebarPanel.add(connectionButton, BorderLayout.SOUTH);
        add(sidebarPanel, BorderLayout.WEST);
        contentPanel.setLayout(new CardLayout()); add(contentPanel, BorderLayout.CENTER);
        sidebarPanel.setBackground(new java.awt.Color(23, 43, 77));
        brandPanel.setBackground(new java.awt.Color(23, 43, 77));
        brandLabel.setForeground(new java.awt.Color(255, 255, 255));
        brandSubtitle.setForeground(new java.awt.Color(255, 255, 255));
        navPanel.setBackground(new java.awt.Color(23, 43, 77));
        dashboardButton.setBackground(new java.awt.Color(29, 78, 216));
        dashboardButton.setForeground(new java.awt.Color(255, 255, 255));
        clientsButton.setBackground(new java.awt.Color(29, 78, 216));
        clientsButton.setForeground(new java.awt.Color(255, 255, 255));
        employeesButton.setBackground(new java.awt.Color(29, 78, 216));
        employeesButton.setForeground(new java.awt.Color(255, 255, 255));
        paymentMethodsButton.setBackground(new java.awt.Color(29, 78, 216));
        paymentMethodsButton.setForeground(new java.awt.Color(255, 255, 255));
        salesButton.setBackground(new java.awt.Color(29, 78, 216));
        salesButton.setForeground(new java.awt.Color(255, 255, 255));
        paymentsButton.setBackground(new java.awt.Color(29, 78, 216));
        paymentsButton.setForeground(new java.awt.Color(255, 255, 255));
        collectionsButton.setBackground(new java.awt.Color(29, 78, 216));
        collectionsButton.setForeground(new java.awt.Color(255, 255, 255));
        reportsButton.setBackground(new java.awt.Color(29, 78, 216));
        reportsButton.setForeground(new java.awt.Color(255, 255, 255));
        contentPanel.setBackground(new java.awt.Color(238, 244, 252));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel sidebarPanel;
    private JPanel brandPanel;
    private JLabel brandLabel;
    private JLabel brandSubtitle;
    private JPanel navPanel;
    private JButton dashboardButton;
    private JButton clientsButton;
    private JButton employeesButton;
    private JButton paymentMethodsButton;
    private JButton salesButton;
    private JButton paymentsButton;
    private JButton collectionsButton;
    private JButton reportsButton;
    private JButton connectionButton;
    private JPanel contentPanel;
    // End of variables declaration//GEN-END:variables
}
