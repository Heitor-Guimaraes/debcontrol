package br.com.debcontrol.ui.dialogs;

import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.service.ClienteService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClienteDialog extends JDialog {
    private final ClienteService service;
    private final Cliente cliente;
    private boolean salvo;

    /** Construtor usado exclusivamente pela aba Design do NetBeans. */
    public ClienteDialog() {
        this.service = null;
        this.cliente = new Cliente();
        initComponents();
        configurar();
        preencher();
    }

    public ClienteDialog(Window owner, ClienteService service, Cliente cliente) {
        super(owner, cliente == null ? "Novo cliente" : "Editar cliente", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.cliente = cliente == null ? new Cliente() : cliente;
        initComponents();
        configurar();
        preencher();
    }

    public boolean isSalvo() { return salvo; }

    private void configurar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(520, 380));
        setLocationRelativeTo(getOwner());
        rootPanel.setBorder(new EmptyBorder(22, 22, 22, 22));
        formPanel.setBorder(new EmptyBorder(16, 0, 16, 0));
        UiTheme.stylePrimaryButton(saveButton);
        UiTheme.configureDialog(this, saveButton);
        UiTheme.styleSecondaryButton(cancelButton);
        saveButton.addActionListener(event -> salvar());
        cancelButton.addActionListener(event -> dispose());
    }

    private void preencher() {
        nameField.setText(cliente.getNome());
        cpfField.setText(cliente.getCpf());
        phoneField.setText(cliente.getTelefone());
        addressField.setText(cliente.getEndereco());
    }

    private void salvar() {
        try {
            cliente.setNome(nameField.getText());
            cliente.setCpf(cpfField.getText());
            cliente.setTelefone(phoneField.getText());
            cliente.setEndereco(addressField.getText());
            service.salvar(cliente);
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
        titleLabel = new JLabel("Dados do cliente");
        formPanel = new JPanel(new GridLayout(4, 2, 12, 12));
        nameLabel = new JLabel("Nome completo *");
        nameField = new JTextField();
        cpfLabel = new JLabel("CPF *");
        cpfField = new JTextField();
        phoneLabel = new JLabel("Telefone *");
        phoneField = new JTextField();
        addressLabel = new JLabel("Endereço");
        addressField = new JTextField();
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cancelButton = new JButton("Cancelar");
        saveButton = new JButton("Salvar cliente");

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(cpfLabel); formPanel.add(cpfField);
        formPanel.add(phoneLabel); formPanel.add(phoneField);
        formPanel.add(addressLabel); formPanel.add(addressField);
        rootPanel.add(formPanel, BorderLayout.CENTER);
        actionsPanel.add(cancelButton); actionsPanel.add(saveButton);
        rootPanel.add(actionsPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel);
        pack();
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
    private JLabel cpfLabel;
    private JTextField cpfField;
    private JLabel phoneLabel;
    private JTextField phoneField;
    private JLabel addressLabel;
    private JTextField addressField;
    private JPanel actionsPanel;
    private JButton cancelButton;
    private JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
