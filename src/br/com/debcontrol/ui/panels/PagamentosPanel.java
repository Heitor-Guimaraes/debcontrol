package br.com.debcontrol.ui.panels;

import br.com.debcontrol.model.Pagamento;
import br.com.debcontrol.model.VendaFiada;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.service.FormaPagamentoService;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.ui.dialogs.PagamentoDialog;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentosPanel extends JPanel {
    private final FinanceiroService service;
    private final FuncionarioService funcionarioService;
    private final FormaPagamentoService formaPagamentoService;
    private final Runnable onDataChanged;
    private final NonEditableTableModel debtsModel = new NonEditableTableModel(
            new Object[]{"Venda", "Cliente", "Saldo", "Vencimento", "Situação"});
    private final NonEditableTableModel paymentsModel = new NonEditableTableModel(
            new Object[]{"ID", "Venda", "Cliente", "Funcionário", "Forma", "Valor", "Data", "Observação"});
    private List<VendaFiada> dividas = new ArrayList<>();

    /** Construtor usado exclusivamente pela aba Design do NetBeans. */
    public PagamentosPanel() {
        this.service = null;
        this.funcionarioService = null;
        this.formaPagamentoService = null;
        this.onDataChanged = () -> { };
        initComponents();
        configurar();
    }

    public PagamentosPanel(FinanceiroService service, FuncionarioService funcionarioService,
                           FormaPagamentoService formaPagamentoService, Runnable onDataChanged) {
        this.service = service;
        this.funcionarioService = funcionarioService;
        this.formaPagamentoService = formaPagamentoService;
        this.onDataChanged = onDataChanged;
        initComponents(); configurar();
    }

    public void atualizar() {
        try {
            dividas = service.listarVendas(searchField.getText(), true);
            debtsModel.setRowCount(0);
            for (VendaFiada venda : dividas) debtsModel.addRow(new Object[]{venda.getIdVenda(), venda.getClienteNome(),
                    Formatador.moeda(venda.getSaldo()), Formatador.data(venda.getDataVencimento()),
                    venda.isAtrasada() ? "Atrasada" : "Em aberto"});
            paymentsModel.setRowCount(0);
            for (Pagamento pagamento : service.listarPagamentos(searchField.getText())) paymentsModel.addRow(new Object[]{
                    pagamento.getIdPagamento(), pagamento.getIdVenda(), pagamento.getClienteNome(),
                    pagamento.getFuncionarioNome(), pagamento.getFormaPagamentoNome(),
                    Formatador.moeda(pagamento.getValorPago()),
                    Formatador.data(pagamento.getDataPagamento()), pagamento.getObservacao()});
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void configurar() {
        setBackground(UiTheme.BACKGROUND); setBorder(new EmptyBorder(24, 24, 24, 24));
        debtsTable.setModel(debtsModel); paymentsTable.setModel(paymentsModel);
        UiTheme.styleTable(debtsTable); UiTheme.styleTable(paymentsTable);
        // Sem este limite, cada tabela pede 400 px de altura e o histórico fica fora da área visível.
        debtsScroll.setPreferredSize(new Dimension(400, 150));
        paymentsScroll.setPreferredSize(new Dimension(400, 150));
        UiTheme.stylePrimaryButton(payButton); UiTheme.styleSecondaryButton(searchButton);
        payButton.addActionListener(event -> pagar()); searchButton.addActionListener(event -> atualizar());
        searchField.addActionListener(event -> atualizar());
    }

    private void pagar() {
        try {
            int row = debtsTable.getSelectedRow();
            if (row < 0) throw new br.com.debcontrol.exception.RegraNegocioException("Selecione uma dívida aberta.");
            VendaFiada venda = dividas.get(debtsTable.convertRowIndexToModel(row));
            PagamentoDialog dialog = new PagamentoDialog(
                    SwingUtilities.getWindowAncestor(this), service, funcionarioService, formaPagamentoService, venda);
            dialog.setVisible(true);
            if (dialog.isSalvo()) { atualizar(); onDataChanged.run(); }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        topPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel = new JPanel(new BorderLayout()); titleLabel = new JLabel("Pagamentos"); payButton = new JButton("Registrar pagamento");
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); searchLabel = new JLabel("Buscar:");
        searchField = new JTextField(24); searchButton = new JButton("Pesquisar");
        listsPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        debtsPanel = new JPanel(new BorderLayout(0, 6)); debtsLabel = new JLabel("Dívidas abertas");
        debtsScroll = new JScrollPane(); debtsTable = new JTable();
        paymentsPanel = new JPanel(new BorderLayout(0, 6)); paymentsLabel = new JLabel("Histórico de pagamentos");
        paymentsScroll = new JScrollPane(); paymentsTable = new JTable();
        setLayout(new BorderLayout(0, 16)); titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerPanel.add(titleLabel, BorderLayout.WEST); headerPanel.add(payButton, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        toolbarPanel.add(searchLabel); toolbarPanel.add(searchField); toolbarPanel.add(searchButton);
        topPanel.add(toolbarPanel, BorderLayout.SOUTH); add(topPanel, BorderLayout.NORTH);
        debtsPanel.add(debtsLabel, BorderLayout.NORTH); debtsScroll.setViewportView(debtsTable);
        debtsPanel.add(debtsScroll, BorderLayout.CENTER); listsPanel.add(debtsPanel);
        paymentsPanel.add(paymentsLabel, BorderLayout.NORTH); paymentsScroll.setViewportView(paymentsTable);
        paymentsPanel.add(paymentsScroll, BorderLayout.CENTER); listsPanel.add(paymentsPanel);
        add(listsPanel, BorderLayout.CENTER);
        setBackground(new java.awt.Color(238, 244, 252));
        topPanel.setBackground(new java.awt.Color(238, 244, 252));
        headerPanel.setBackground(new java.awt.Color(238, 244, 252));
        titleLabel.setForeground(new java.awt.Color(30, 64, 175));
        payButton.setBackground(new java.awt.Color(21, 108, 61));
        payButton.setForeground(new java.awt.Color(255, 255, 255));
        toolbarPanel.setBackground(new java.awt.Color(221, 232, 249));
        searchButton.setBackground(new java.awt.Color(229, 237, 252));
        searchButton.setForeground(new java.awt.Color(30, 64, 175));
        listsPanel.setBackground(new java.awt.Color(238, 244, 252));
        debtsPanel.setBackground(new java.awt.Color(238, 244, 252));
        paymentsPanel.setBackground(new java.awt.Color(238, 244, 252));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel topPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JButton payButton;
    private JPanel toolbarPanel;
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel listsPanel;
    private JPanel debtsPanel;
    private JLabel debtsLabel;
    private JScrollPane debtsScroll;
    private JTable debtsTable;
    private JPanel paymentsPanel;
    private JLabel paymentsLabel;
    private JScrollPane paymentsScroll;
    private JTable paymentsTable;
    // End of variables declaration//GEN-END:variables
}
