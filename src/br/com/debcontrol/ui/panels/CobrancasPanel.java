package br.com.debcontrol.ui.panels;

import br.com.debcontrol.model.Cobranca;
import br.com.debcontrol.service.CobrancaService;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.ui.dialogs.CobrancaDialog;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CobrancasPanel extends JPanel {
    private final CobrancaService cobrancaService;
    private final FinanceiroService financeiroService;
    private final FuncionarioService funcionarioService;
    private final Runnable onDataChanged;
    private final NonEditableTableModel model = new NonEditableTableModel(
            new Object[]{"ID", "Venda", "Cliente", "Funcionário", "Data", "Meio", "Resultado", "Próximo contato"});

    public CobrancasPanel() {
        this.cobrancaService = null;
        this.financeiroService = null;
        this.funcionarioService = null;
        this.onDataChanged = () -> { };
        initComponents();
        configurar();
    }

    public CobrancasPanel(CobrancaService cobrancaService, FinanceiroService financeiroService,
                          FuncionarioService funcionarioService, Runnable onDataChanged) {
        this.cobrancaService = cobrancaService;
        this.financeiroService = financeiroService;
        this.funcionarioService = funcionarioService;
        this.onDataChanged = onDataChanged;
        initComponents();
        configurar();
    }

    public void atualizar() {
        try {
            model.setRowCount(0);
            for (Cobranca item : cobrancaService.listar(searchField.getText())) {
                model.addRow(new Object[]{item.getIdCobranca(), item.getIdVenda(), item.getClienteNome(),
                        item.getFuncionarioNome(), Formatador.data(item.getDataCobranca()),
                        item.getMeioContato(), item.getResultado(), Formatador.data(item.getProximoContato())});
            }
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }

    private void configurar() {
        setBackground(UiTheme.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        table.setModel(model); UiTheme.styleTable(table);
        UiTheme.stylePrimaryButton(newButton);
        UiTheme.styleSecondaryButton(searchButton);
        newButton.addActionListener(event -> abrir());
        searchButton.addActionListener(event -> atualizar());
        searchField.addActionListener(event -> atualizar());
    }

    private void abrir() {
        CobrancaDialog dialog = new CobrancaDialog(
                SwingUtilities.getWindowAncestor(this), cobrancaService, financeiroService, funcionarioService);
        dialog.setVisible(true);
        if (dialog.isSalvo()) { atualizar(); onDataChanged.run(); }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        topPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Cobranças"); newButton = new JButton("Registrar cobrança");
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchField = new JTextField(24); searchButton = new JButton("Pesquisar");
        tableScroll = new JScrollPane(); table = new JTable();
        setLayout(new BorderLayout(0, 16));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerPanel.add(titleLabel, BorderLayout.WEST); headerPanel.add(newButton, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        toolbarPanel.add(new JLabel("Buscar:")); toolbarPanel.add(searchField); toolbarPanel.add(searchButton);
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
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel topPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JButton newButton;
    private JPanel toolbarPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JScrollPane tableScroll;
    private JTable table;
    // End of variables declaration//GEN-END:variables
}
