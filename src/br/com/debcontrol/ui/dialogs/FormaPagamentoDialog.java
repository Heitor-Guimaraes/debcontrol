package br.com.debcontrol.ui.dialogs;

import br.com.debcontrol.model.FormaPagamento;
import br.com.debcontrol.service.FormaPagamentoService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormaPagamentoDialog extends JDialog {
    private final FormaPagamentoService service;
    private final FormaPagamento forma;
    private boolean salvo;

    public FormaPagamentoDialog() {
        this.service = null;
        this.forma = new FormaPagamento();
        initComponents();
        configurar();
    }

    public FormaPagamentoDialog(Window owner, FormaPagamentoService service, FormaPagamento forma) {
        super(owner, forma == null ? "Nova forma de pagamento" : "Editar forma de pagamento",
                ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.forma = forma == null ? new FormaPagamento() : forma;
        initComponents();
        configurar();
        preencher();
    }

    public boolean isSalvo() { return salvo; }

    private void configurar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(520, 330));
        setLocationRelativeTo(getOwner());
        rootPanel.setBorder(new EmptyBorder(22, 22, 22, 22));
        UiTheme.stylePrimaryButton(saveButton);
        UiTheme.configureDialog(this, saveButton);
        UiTheme.styleSecondaryButton(cancelButton);
        saveButton.addActionListener(event -> salvar());
        cancelButton.addActionListener(event -> dispose());
    }

    private void preencher() {
        nameField.setText(forma.getNome());
        descriptionField.setText(forma.getDescricao());
    }

    private void salvar() {
        try {
            forma.setNome(nameField.getText());
            forma.setDescricao(descriptionField.getText());
            service.salvar(forma);
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
        titleLabel = new JLabel("Forma de pagamento");
        formPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        nameLabel = new JLabel("Nome *"); nameField = new JTextField();
        descriptionLabel = new JLabel("Descrição"); descriptionField = new JTextField();
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cancelButton = new JButton("Cancelar"); saveButton = new JButton("Salvar");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        formPanel.add(nameLabel); formPanel.add(nameField);
        formPanel.add(descriptionLabel); formPanel.add(descriptionField);
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
    private JLabel descriptionLabel;
    private JTextField descriptionField;
    private JPanel actionsPanel;
    private JButton cancelButton;
    private JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
