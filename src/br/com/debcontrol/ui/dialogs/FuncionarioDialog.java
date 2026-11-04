package br.com.debcontrol.ui.dialogs;

import br.com.debcontrol.model.Funcionario;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FuncionarioDialog extends JDialog {
    private final FuncionarioService service;
    private final Funcionario funcionario;
    private boolean salvo;

    public FuncionarioDialog() {
        this.service = null;
        this.funcionario = new Funcionario();
        initComponents();
        configurar();
    }

    public FuncionarioDialog(Window owner, FuncionarioService service, Funcionario funcionario) {
        super(owner, funcionario == null ? "Novo funcionário" : "Editar funcionário",
                ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.funcionario = funcionario == null ? new Funcionario() : funcionario;
        initComponents();
        configurar();
        preencher();
    }

    public boolean isSalvo() { return salvo; }

    private void configurar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(520, 410));
        setLocationRelativeTo(getOwner());
        rootPanel.setBorder(new EmptyBorder(22, 22, 22, 22));
        UiTheme.stylePrimaryButton(saveButton);
        UiTheme.configureDialog(this, saveButton);
        UiTheme.styleSecondaryButton(cancelButton);
        saveButton.addActionListener(event -> salvar());
        cancelButton.addActionListener(event -> dispose());
    }

    private void preencher() {
        nameField.setText(funcionario.getNome());
        roleField.setText(funcionario.getCargo());
        loginField.setText(funcionario.getLogin());
        if (funcionario.getIdFuncionario() != null) {
            passwordLabel.setText("Nova senha (opcional)");
            passwordField.setToolTipText("Deixe em branco para manter a senha atual");
        }
    }

    private void salvar() {
        try {
            funcionario.setNome(nameField.getText());
            funcionario.setCargo(roleField.getText());
            funcionario.setLogin(loginField.getText());
            service.salvar(funcionario, new String(passwordField.getPassword()));
            salvo = true;
            dispose();
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        rootPanel = new JPanel(new BorderLayout(0, 14));
        titleLabel = new JLabel("Dados do funcionário");
        formPanel = new JPanel(new GridLayout(4, 2, 12, 12));
        nameLabel = new JLabel("Nome *"); nameField = new JTextField();
        roleLabel = new JLabel("Cargo *"); roleField = new JTextField();
        loginLabel = new JLabel("Login *"); loginField = new JTextField();
        passwordLabel = new JLabel("Senha *"); passwordField = new JPasswordField();
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cancelButton = new JButton("Cancelar"); saveButton = new JButton("Salvar funcionário");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(roleLabel); formPanel.add(roleField);
        formPanel.add(loginLabel); formPanel.add(loginField);
        formPanel.add(passwordLabel); formPanel.add(passwordField);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        actionsPanel.add(cancelButton); actionsPanel.add(saveButton);
        rootPanel.add(actionsPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel); pack();
        rootPanel.setBackground(new java.awt.Color(255, 255, 255));
        titleLabel.setForeground(new java.awt.Color(30, 64, 175));
        formPanel.setBackground(new java.awt.Color(255, 255, 255));
        actionsPanel.setBackground(new java.awt.Color(255, 255, 255));
        cancelButton.setBackground(new java.awt.Color(229, 237, 252));
        cancelButton.setForeground(new java.awt.Color(30, 64, 175));
        saveButton.setBackground(new java.awt.Color(29, 78, 216));
        saveButton.setForeground(new java.awt.Color(255, 255, 255));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel rootPanel;
    private JLabel titleLabel;
    private JPanel formPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel roleLabel;
    private JTextField roleField;
    private JLabel loginLabel;
    private JTextField loginField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JPanel actionsPanel;
    private JButton cancelButton;
    private JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
