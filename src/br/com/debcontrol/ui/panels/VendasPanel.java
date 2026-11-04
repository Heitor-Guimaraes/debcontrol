package br.com.debcontrol.ui.panels;

import br.com.debcontrol.model.VendaFiada;
import br.com.debcontrol.service.ClienteService;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.ui.dialogs.VendaDialog;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VendasPanel extends JPanel {
    private final ClienteService clienteService;
    private final FinanceiroService financeiroService;
    private final FuncionarioService funcionarioService;
    private final Runnable onDataChanged;
    private final NonEditableTableModel model = new NonEditableTableModel(
            new Object[]{"ID", "Cliente", "Funcionário", "Venda", "Pago", "Saldo", "Vencimento", "Situação"});
    private List<VendaFiada> vendas = new ArrayList<>();

    /** Construtor usado exclusivamente pela aba Design do NetBeans. */
    public VendasPanel() {
        this.clienteService = null;
        this.financeiroService = null;
        this.funcionarioService = null;
        this.onDataChanged = () -> { };
        initComponents();
        configurar();
    }

    public VendasPanel(ClienteService clienteService, FuncionarioService funcionarioService,
                       FinanceiroService financeiroService, Runnable onDataChanged) {
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
        this.financeiroService = financeiroService;
        this.onDataChanged = onDataChanged;
        initComponents();
        configurar();
    }

    public void atualizar() {
        try {
            vendas = financeiroService.listarVendas(searchField.getText(), openOnlyCheck.isSelected());
            model.setRowCount(0);
            for (VendaFiada venda : vendas) {
                String situacao = venda.getStatus().name();
                if (venda.isAtrasada()) situacao = "ATRASADA";
                model.addRow(new Object[]{venda.getIdVenda(), venda.getClienteNome(), venda.getFuncionarioNome(),
                        Formatador.moeda(venda.getValorTotal()),
                        Formatador.moeda(venda.getValorPago()), Formatador.moeda(venda.getSaldo()),
                        Formatador.data(venda.getDataVencimento()), situacao});
            }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void configurar() {
        setBackground(UiTheme.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        table.setModel(model); UiTheme.styleTable(table);
        UiTheme.stylePrimaryButton(newButton);
        UiTheme.styleSecondaryButton(searchButton);
        UiTheme.styleSecondaryButton(cancelSaleButton);
        newButton.addActionListener(event -> novaVenda());
        searchButton.addActionListener(event -> atualizar());
        searchField.addActionListener(event -> atualizar());
        openOnlyCheck.addActionListener(event -> atualizar());
        cancelSaleButton.addActionListener(event -> cancelar());
    }

    private VendaFiada selecionada() {
        int row = table.getSelectedRow();
        if (row < 0) throw new br.com.debcontrol.exception.RegraNegocioException("Selecione uma venda.");
        return vendas.get(table.convertRowIndexToModel(row));
    }

    private void novaVenda() {
        VendaDialog dialog = new VendaDialog(
                SwingUtilities.getWindowAncestor(this), clienteService, funcionarioService, financeiroService);
        dialog.setVisible(true);
        if (dialog.isSalvo()) { atualizar(); onDataChanged.run(); }
    }

    private void cancelar() {
        try {
            VendaFiada venda = selecionada();
            String justificativa = JOptionPane.showInputDialog(this, "Informe a justificativa do cancelamento:");
            if (justificativa != null) {
                financeiroService.cancelarVenda(venda.getIdVenda(), justificativa);
                atualizar(); onDataChanged.run();
            }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        topPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Vendas fiadas"); newButton = new JButton("Nova venda");
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchLabel = new JLabel("Buscar:");
        searchField = new JTextField(24); searchButton = new JButton("Pesquisar");
        openOnlyCheck = new JCheckBox("Somente pendentes", true); cancelSaleButton = new JButton("Cancelar venda");
        tableScroll = new JScrollPane(); table = new JTable();
        setLayout(new BorderLayout(0, 16));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerPanel.add(titleLabel, BorderLayout.WEST); headerPanel.add(newButton, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        toolbarPanel.add(searchLabel); toolbarPanel.add(searchField); toolbarPanel.add(searchButton);
        toolbarPanel.add(openOnlyCheck); toolbarPanel.add(cancelSaleButton);
        topPanel.add(toolbarPanel, BorderLayout.SOUTH); add(topPanel, BorderLayout.NORTH);
        tableScroll.setViewportView(table); add(tableScroll, BorderLayout.CENTER);
        setBackground(new java.awt.Color(238, 244, 252));
        topPanel.setBackground(new java.awt.Color(238, 244, 252));
        headerPanel.setBackground(new java.awt.Color(238, 244, 252));
        titleLabel.setForeground(new java.awt.Color(30, 64, 175));
        newButton.setBackground(new java.awt.Color(29, 78, 216));
        newButton.setForeground(new java.awt.Color(255, 255, 255));
        toolbarPanel.setBackground(new java.awt.Color(221, 232, 249));
        searchButton.setBackground(new java.awt.Color(229, 237, 252));
        searchButton.setForeground(new java.awt.Color(30, 64, 175));
        openOnlyCheck.setBackground(new java.awt.Color(221, 232, 249));
        openOnlyCheck.setForeground(new java.awt.Color(30, 41, 59));
        cancelSaleButton.setBackground(new java.awt.Color(255, 232, 234));
        cancelSaleButton.setForeground(new java.awt.Color(153, 27, 47));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel topPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JButton newButton;
    private JPanel toolbarPanel;
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JCheckBox openOnlyCheck;
    private JButton cancelSaleButton;
    private JScrollPane tableScroll;
    private JTable table;
    // End of variables declaration//GEN-END:variables
}
