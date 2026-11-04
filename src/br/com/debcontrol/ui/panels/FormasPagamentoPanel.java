package br.com.debcontrol.ui.panels;

import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.FormaPagamento;
import br.com.debcontrol.service.FormaPagamentoService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.ui.dialogs.FormaPagamentoDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FormasPagamentoPanel extends JPanel {
    private final FormaPagamentoService service;
    private final Runnable onDataChanged;
    private final NonEditableTableModel model = new NonEditableTableModel(
            new Object[]{"ID", "Nome", "Descrição", "Situação"});
    private List<FormaPagamento> formas = new ArrayList<>();

    public FormasPagamentoPanel() {
        this.service = null;
        this.onDataChanged = () -> { };
        initComponents();
        configurar();
    }

    public FormasPagamentoPanel(FormaPagamentoService service, Runnable onDataChanged) {
        this.service = service;
        this.onDataChanged = onDataChanged;
        initComponents();
        configurar();
    }

    public void atualizar() {
        try {
            formas = service.listar(searchField.getText(), activeOnlyCheck.isSelected());
            model.setRowCount(0);
            for (FormaPagamento item : formas) {
                model.addRow(new Object[]{item.getIdFormaPagamento(), item.getNome(),
                        item.getDescricao(), item.isAtivo() ? "Ativa" : "Inativa"});
            }
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }


    private void excluirSelecionado() {
        try {
            var item = selecionada();
            if (MessageBox.confirm(this, "Excluir definitivamente o cadastro selecionado? Registros com histórico serão preservados.")) {
                service.excluir(item.getIdFormaPagamento());
                atualizar();
                onDataChanged.run();
                MessageBox.success(this, "Cadastro excluído.");
            }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void configurar() {
        setBackground(UiTheme.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        table.setModel(model); UiTheme.styleTable(table);
        UiTheme.stylePrimaryButton(newButton);
        UiTheme.styleSecondaryButton(searchButton);
        UiTheme.styleSecondaryButton(editButton);
        UiTheme.styleSecondaryButton(deactivateButton);
        UiTheme.styleSecondaryButton(deleteButton);
        deleteButton.setForeground(new Color(153, 27, 47));
        deleteButton.setBackground(new Color(255, 232, 234));
        deleteButton.setToolTipText("Exclusão definitiva somente para cadastros sem histórico");
        deleteButton.addActionListener(event -> excluirSelecionado());
        newButton.addActionListener(event -> abrir(null));
        editButton.addActionListener(event -> abrir(selecionada()));
        deactivateButton.addActionListener(event -> desativar());
        searchButton.addActionListener(event -> atualizar());
        searchField.addActionListener(event -> atualizar());
        activeOnlyCheck.addActionListener(event -> atualizar());
    }

    private FormaPagamento selecionada() {
        int row = table.getSelectedRow();
        if (row < 0) throw new RegraNegocioException("Selecione uma forma de pagamento.");
        return formas.get(table.convertRowIndexToModel(row));
    }

    private void abrir(FormaPagamento forma) {
        FormaPagamentoDialog dialog = new FormaPagamentoDialog(
                SwingUtilities.getWindowAncestor(this), service, forma);
        dialog.setVisible(true);
        if (dialog.isSalvo()) { atualizar(); onDataChanged.run(); }
    }

    private void desativar() {
        try {
            FormaPagamento item = selecionada();
            if (MessageBox.confirm(this, "Desativar " + item.getNome() + "?")) {
                service.desativar(item.getIdFormaPagamento());
                atualizar();
                onDataChanged.run();
            }
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        deleteButton = new JButton("Excluir");
        topPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel("Formas de pagamento"); newButton = new JButton("Nova forma");
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchField = new JTextField(22); searchButton = new JButton("Pesquisar");
        activeOnlyCheck = new JCheckBox("Somente ativas", true);
        editButton = new JButton("Editar"); deactivateButton = new JButton("Desativar");
        tableScroll = new JScrollPane(); table = new JTable();
        setLayout(new BorderLayout(0, 16));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerPanel.add(titleLabel, BorderLayout.WEST); headerPanel.add(newButton, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        toolbarPanel.add(new JLabel("Buscar:")); toolbarPanel.add(searchField); toolbarPanel.add(searchButton);
        toolbarPanel.add(activeOnlyCheck); toolbarPanel.add(editButton); toolbarPanel.add(deactivateButton); toolbarPanel.add(deleteButton);
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
        activeOnlyCheck.setBackground(new java.awt.Color(221, 232, 249));
        activeOnlyCheck.setForeground(new java.awt.Color(30, 41, 59));
        editButton.setBackground(new java.awt.Color(229, 237, 252));
        editButton.setForeground(new java.awt.Color(30, 64, 175));
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
    private JTextField searchField;
    private JButton searchButton;
    private JCheckBox activeOnlyCheck;
    private JButton editButton;
    private JButton deactivateButton;
    private JScrollPane tableScroll;
    private JTable table;
    // End of variables declaration//GEN-END:variables
}
