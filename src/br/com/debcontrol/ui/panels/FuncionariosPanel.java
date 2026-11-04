package br.com.debcontrol.ui.panels;

import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.Funcionario;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.ui.dialogs.FuncionarioDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionariosPanel extends JPanel {
    private final FuncionarioService service;
    private final Runnable onDataChanged;
    private final NonEditableTableModel model = new NonEditableTableModel(
            new Object[]{"ID", "Nome", "Cargo", "Login", "Situação"});
    private List<Funcionario> funcionarios = new ArrayList<>();

    public FuncionariosPanel() {
        this.service = null;
        this.onDataChanged = () -> { };
        initComponents();
        configurar();
    }

    public FuncionariosPanel(FuncionarioService service, Runnable onDataChanged) {
        this.service = service;
        this.onDataChanged = onDataChanged;
        initComponents();
        configurar();
    }

    public void atualizar() {
        try {
            funcionarios = service.listar(searchField.getText(), activeOnlyCheck.isSelected());
            model.setRowCount(0);
            for (Funcionario item : funcionarios) {
                model.addRow(new Object[]{item.getIdFuncionario(), item.getNome(), item.getCargo(),
                        item.getLogin(), item.isAtivo() ? "Ativo" : "Inativo"});
            }
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }


    private void excluirSelecionado() {
        try {
            var item = selecionado();
            if (MessageBox.confirm(this, "Excluir definitivamente o cadastro selecionado? Registros com histórico serão preservados.")) {
                service.excluir(item.getIdFuncionario());
                atualizar();
                onDataChanged.run();
                MessageBox.success(this, "Cadastro excluído.");
            }
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void configurar() {
        setBackground(UiTheme.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        table.setModel(model);
        UiTheme.styleTable(table);
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
        editButton.addActionListener(event -> editarSelecionado());
        deactivateButton.addActionListener(event -> desativar());
        searchButton.addActionListener(event -> atualizar());
        searchField.addActionListener(event -> atualizar());
        activeOnlyCheck.addActionListener(event -> atualizar());
    }

    private Funcionario selecionado() {
        int row = table.getSelectedRow();
        if (row < 0) throw new RegraNegocioException("Selecione um funcionário.");
        return funcionarios.get(table.convertRowIndexToModel(row));
    }

    private void editarSelecionado() {
        // Edita uma cópia nova: cancelar o formulário não altera o objeto exibido na lista.
        try { abrir(service.buscar(selecionado().getIdFuncionario())); }
        catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void abrir(Funcionario funcionario) {
        FuncionarioDialog dialog = new FuncionarioDialog(
                SwingUtilities.getWindowAncestor(this), service, funcionario);
        dialog.setVisible(true);
        if (dialog.isSalvo()) { atualizar(); onDataChanged.run(); }
    }

    private void desativar() {
        try {
            Funcionario item = selecionado();
            if (MessageBox.confirm(this, "Desativar " + item.getNome() + "?")) {
                service.desativar(item.getIdFuncionario());
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
        titleLabel = new JLabel("Funcionários"); newButton = new JButton("Novo funcionário");
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchField = new JTextField(22); searchButton = new JButton("Pesquisar");
        activeOnlyCheck = new JCheckBox("Somente ativos", true);
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
