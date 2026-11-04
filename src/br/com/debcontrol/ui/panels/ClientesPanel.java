package br.com.debcontrol.ui.panels;

import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.service.ClienteService;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.ui.dialogs.ClienteDialog;
import br.com.debcontrol.ui.dialogs.ExtratoClienteDialog;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ClientesPanel extends JPanel {
    private final ClienteService service;
    private final FinanceiroService financeiroService;
    private final Runnable onDataChanged;
    private final NonEditableTableModel tableModel = new NonEditableTableModel(
            new Object[]{"ID", "Nome", "CPF", "Telefone", "Saldo", "Situação"});
    private List<Cliente> clientes = new ArrayList<>();

    /** Construtor usado exclusivamente pela aba Design do NetBeans. */
    public ClientesPanel() {
        this.service = null;
        this.financeiroService = null;
        this.onDataChanged = () -> { };
        initComponents();
        configurar();
    }

    public ClientesPanel(ClienteService service, FinanceiroService financeiroService, Runnable onDataChanged) {
        this.service = service;
        this.financeiroService = financeiroService;
        this.onDataChanged = onDataChanged;
        initComponents();
        configurar();
    }

    public void atualizar() {
        try {
            clientes = service.listar(searchField.getText(), activeOnlyCheck.isSelected());
            tableModel.setRowCount(0);
            for (Cliente cliente : clientes) {
                var saldo = financeiroService.saldoCliente(cliente.getIdCliente());
                tableModel.addRow(new Object[]{cliente.getIdCliente(), cliente.getNome(), cliente.getCpf(), cliente.getTelefone(),
                        Formatador.moeda(saldo),
                        !cliente.isAtivo() ? "Inativo" : saldo.signum() > 0 ? "Devendo" : "Em dia"});
            }
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }


    private void excluirSelecionado() {
        try {
            var item = selecionado();
            if (MessageBox.confirm(this, "Excluir definitivamente o cadastro selecionado? Registros com histórico serão preservados.")) {
                service.excluir(item.getIdCliente());
                atualizar();
                onDataChanged.run();
                MessageBox.success(this, "Cadastro excluído.");
            }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void configurar() {
        setBackground(UiTheme.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        table.setModel(tableModel);
        UiTheme.styleTable(table);
        UiTheme.stylePrimaryButton(newButton);
        UiTheme.styleSecondaryButton(editButton);
        UiTheme.styleSecondaryButton(deactivateButton);
        UiTheme.styleSecondaryButton(deleteButton);
        deleteButton.setForeground(new Color(153, 27, 47));
        deleteButton.setBackground(new Color(255, 232, 234));
        deleteButton.setToolTipText("Exclusão definitiva somente para cadastros sem histórico");
        deleteButton.addActionListener(event -> excluirSelecionado());
        UiTheme.styleSecondaryButton(statementButton);
        UiTheme.styleSecondaryButton(searchButton);
        newButton.addActionListener(event -> abrirDialog(null));
        editButton.addActionListener(event -> editarSelecionado());
        deactivateButton.addActionListener(event -> desativarSelecionado());
        statementButton.addActionListener(event -> abrirExtrato());
        searchButton.addActionListener(event -> atualizar());
        searchField.addActionListener(event -> atualizar());
        activeOnlyCheck.addActionListener(event -> atualizar());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) editarSelecionado();
            }
        });
    }

    private Cliente selecionado() {
        int row = table.getSelectedRow();
        if (row < 0) throw new br.com.debcontrol.exception.RegraNegocioException("Selecione um cliente.");
        return clientes.get(table.convertRowIndexToModel(row));
    }

    private void editarSelecionado() {
        try { abrirDialog(service.buscar(selecionado().getIdCliente())); }
        catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void abrirDialog(Cliente cliente) {
        ClienteDialog dialog = new ClienteDialog(SwingUtilities.getWindowAncestor(this), service, cliente);
        dialog.setVisible(true);
        if (dialog.isSalvo()) { atualizar(); onDataChanged.run(); }
    }

    private void desativarSelecionado() {
        try {
            Cliente cliente = selecionado();
            if (MessageBox.confirm(this, "Desativar o cliente " + cliente.getNome() + "?")) {
                service.desativar(cliente.getIdCliente());
                atualizar();
                onDataChanged.run();
            }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void abrirExtrato() {
        try {
            Cliente cliente = selecionado();
            new ExtratoClienteDialog(SwingUtilities.getWindowAncestor(this), cliente, financeiroService).setVisible(true);
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        deleteButton = new JButton("Excluir");
        topPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Clientes");
        newButton = new JButton("Novo cliente");
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchLabel = new JLabel("Buscar:");
        searchField = new JTextField(24);
        searchButton = new JButton("Pesquisar");
        activeOnlyCheck = new JCheckBox("Somente ativos", true);
        editButton = new JButton("Editar");
        statementButton = new JButton("Extrato");
        deactivateButton = new JButton("Desativar");
        tableScroll = new JScrollPane();
        table = new JTable();

        setLayout(new BorderLayout(0, 16));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(newButton, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        toolbarPanel.add(searchLabel); toolbarPanel.add(searchField); toolbarPanel.add(searchButton);
        toolbarPanel.add(activeOnlyCheck); toolbarPanel.add(editButton); toolbarPanel.add(statementButton); toolbarPanel.add(deactivateButton); toolbarPanel.add(deleteButton);
        topPanel.add(toolbarPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        tableScroll.setViewportView(table);
        add(tableScroll, BorderLayout.CENTER);
        setBackground(new java.awt.Color(238, 244, 252));
        topPanel.setBackground(new java.awt.Color(238, 244, 252));
        headerPanel.setBackground(new java.awt.Color(238, 244, 252));
        titleLabel.setForeground(new java.awt.Color(30, 64, 175));
        newButton.setBackground(new java.awt.Color(29, 78, 216));
        newButton.setForeground(new java.awt.Color(255, 255, 255));
        toolbarPanel.setBackground(new java.awt.Color(221, 232, 249));
        searchButton.setBackground(new java.awt.Color(229, 237, 252));
        searchButton.setForeground(new java.awt.Color(30, 64, 175));
        activeOnlyCheck.setBackground(new java.awt.Color(221, 232, 249));
        activeOnlyCheck.setForeground(new java.awt.Color(30, 41, 59));
        editButton.setBackground(new java.awt.Color(229, 237, 252));
        editButton.setForeground(new java.awt.Color(30, 64, 175));
        statementButton.setBackground(new java.awt.Color(229, 237, 252));
        statementButton.setForeground(new java.awt.Color(30, 64, 175));
        deactivateButton.setBackground(new java.awt.Color(255, 232, 234));
        deactivateButton.setForeground(new java.awt.Color(153, 27, 47));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton deleteButton;
    private JPanel topPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JButton newButton;
    private JPanel toolbarPanel;
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JCheckBox activeOnlyCheck;
    private JButton editButton;
    private JButton statementButton;
    private JButton deactivateButton;
    private JScrollPane tableScroll;
    private JTable table;
    // End of variables declaration//GEN-END:variables
}
