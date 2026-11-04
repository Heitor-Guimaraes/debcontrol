package br.com.debcontrol.ui.dialogs;

import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.model.Funcionario;
import br.com.debcontrol.service.ClienteService;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class VendaDialog extends JDialog {
    private final FinanceiroService financeiroService;
    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;
    private boolean salvo;

    public VendaDialog() {
        this.financeiroService = null;
        this.clienteService = null;
        this.funcionarioService = null;
        initComponents();
        configurar();
    }

    public VendaDialog(Window owner, ClienteService clienteService,
                       FuncionarioService funcionarioService, FinanceiroService financeiroService) {
        super(owner, "Nova venda fiada", ModalityType.APPLICATION_MODAL);
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
        this.financeiroService = financeiroService;
        initComponents();
        configurar();
        carregarOpcoes();
    }

    public boolean isSalvo() { return salvo; }

    private void configurar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(620, 540));
        setLocationRelativeTo(getOwner());
        rootPanel.setBorder(new EmptyBorder(22, 22, 22, 22));
        UiTheme.stylePrimaryButton(saveButton);
        UiTheme.configureDialog(this, saveButton);
        UiTheme.styleSecondaryButton(cancelButton);
        saleDateField.setText(Formatador.data(LocalDate.now()));
        dueDateField.setText(Formatador.data(LocalDate.now().plusMonths(1)));
        saveButton.addActionListener(event -> salvar());
        cancelButton.addActionListener(event -> dispose());
    }

    private void carregarOpcoes() {
        DefaultComboBoxModel<Cliente> clientes = new DefaultComboBoxModel<>();
        for (Cliente cliente : clienteService.listar("", true)) clientes.addElement(cliente);
        clientCombo.setModel(clientes);

        DefaultComboBoxModel<Funcionario> funcionarios = new DefaultComboBoxModel<>();
        for (Funcionario funcionario : funcionarioService.listar("", true)) funcionarios.addElement(funcionario);
        employeeCombo.setModel(funcionarios);
    }

    private void salvar() {
        try {
            Cliente cliente = (Cliente) clientCombo.getSelectedItem();
            Funcionario funcionario = (Funcionario) employeeCombo.getSelectedItem();
            if (cliente == null) throw new RegraNegocioException("Cadastre um cliente ativo antes da venda.");
            if (funcionario == null) throw new RegraNegocioException("Cadastre um funcionário ativo antes da venda.");
            financeiroService.registrarVenda(
                    cliente.getIdCliente(),
                    funcionario.getIdFuncionario(),
                    Formatador.parseMoeda(valueField.getText(), "Valor da venda"),
                    Formatador.parseData(saleDateField.getText(), "Data da venda"),
                    Formatador.parseData(dueDateField.getText(), "Vencimento"),
                    descriptionArea.getText(),
                    observationField.getText());
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
        titleLabel = new JLabel("Registrar venda fiada");
        formPanel = new JPanel(new GridLayout(7, 2, 12, 12));
        clientLabel = new JLabel("Cliente *"); clientCombo = new JComboBox<>();
        employeeLabel = new JLabel("Funcionário *"); employeeCombo = new JComboBox<>();
        valueLabel = new JLabel("Valor *"); valueField = new JTextField();
        saleDateLabel = new JLabel("Data da venda *"); saleDateField = new JTextField();
        dueDateLabel = new JLabel("Vencimento *"); dueDateField = new JTextField();
        descriptionLabel = new JLabel("Descrição da compra *"); descriptionScroll = new JScrollPane();
        descriptionArea = new JTextArea(3, 20);
        observationLabel = new JLabel("Observação"); observationField = new JTextField();
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cancelButton = new JButton("Cancelar"); saveButton = new JButton("Registrar venda");

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        formPanel.add(clientLabel); formPanel.add(clientCombo);
        formPanel.add(employeeLabel); formPanel.add(employeeCombo);
        formPanel.add(valueLabel); formPanel.add(valueField);
        formPanel.add(saleDateLabel); formPanel.add(saleDateField);
        formPanel.add(dueDateLabel); formPanel.add(dueDateField);
        descriptionLabel.setLabelFor(descriptionArea);
        descriptionScroll.setViewportView(descriptionArea);
        formPanel.add(descriptionLabel); formPanel.add(descriptionScroll);
        formPanel.add(observationLabel); formPanel.add(observationField);
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
    private JLabel clientLabel;
    private JComboBox<Cliente> clientCombo;
    private JLabel employeeLabel;
    private JComboBox<Funcionario> employeeCombo;
    private JLabel valueLabel;
    private JTextField valueField;
    private JLabel saleDateLabel;
    private JTextField saleDateField;
    private JLabel dueDateLabel;
    private JTextField dueDateField;
    private JLabel descriptionLabel;
    private JScrollPane descriptionScroll;
    private JTextArea descriptionArea;
    private JLabel observationLabel;
    private JTextField observationField;
    private JPanel actionsPanel;
    private JButton cancelButton;
    private JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
