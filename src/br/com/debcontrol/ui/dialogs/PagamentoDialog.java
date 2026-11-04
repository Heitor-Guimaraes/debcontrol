package br.com.debcontrol.ui.dialogs;

import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.FormaPagamento;
import br.com.debcontrol.model.Funcionario;
import br.com.debcontrol.model.VendaFiada;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.service.FormaPagamentoService;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class PagamentoDialog extends JDialog {
    private final FinanceiroService financeiroService;
    private final FuncionarioService funcionarioService;
    private final FormaPagamentoService formaPagamentoService;
    private final VendaFiada venda;
    private boolean salvo;

    public PagamentoDialog() {
        this.financeiroService = null;
        this.funcionarioService = null;
        this.formaPagamentoService = null;
        this.venda = null;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public PagamentoDialog(Window owner, FinanceiroService financeiroService,
                           FuncionarioService funcionarioService,
                           FormaPagamentoService formaPagamentoService, VendaFiada venda) {
        super(owner, "Registrar pagamento", ModalityType.APPLICATION_MODAL);
        this.financeiroService = financeiroService;
        this.funcionarioService = funcionarioService;
        this.formaPagamentoService = formaPagamentoService;
        this.venda = venda;
        initComponents();
        configurar();
        carregarOpcoes();
    }

    public boolean isSalvo() { return salvo; }

    private void configurar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(560, 460));
        setLocationRelativeTo(getOwner());
        rootPanel.setBorder(new EmptyBorder(22, 22, 22, 22));
        clientValue.setText(venda.getClienteNome());
        debtValue.setText("Venda #" + venda.getIdVenda() + " - Saldo " + Formatador.moeda(venda.getSaldo()));
        dateField.setText(Formatador.data(LocalDate.now()));
        valueField.setText(venda.getSaldo().toPlainString().replace('.', ','));
        UiTheme.stylePrimaryButton(saveButton);
        UiTheme.configureDialog(this, saveButton);
        UiTheme.styleSecondaryButton(cancelButton);
        saveButton.addActionListener(event -> salvar());
        cancelButton.addActionListener(event -> dispose());
    }

    private void carregarOpcoes() {
        DefaultComboBoxModel<Funcionario> funcionarios = new DefaultComboBoxModel<>();
        for (Funcionario item : funcionarioService.listar("", true)) funcionarios.addElement(item);
        employeeCombo.setModel(funcionarios);

        DefaultComboBoxModel<FormaPagamento> formas = new DefaultComboBoxModel<>();
        for (FormaPagamento item : formaPagamentoService.listar("", true)) formas.addElement(item);
        paymentMethodCombo.setModel(formas);
    }

    private void salvar() {
        try {
            Funcionario funcionario = (Funcionario) employeeCombo.getSelectedItem();
            FormaPagamento forma = (FormaPagamento) paymentMethodCombo.getSelectedItem();
            if (funcionario == null) throw new RegraNegocioException("Selecione um funcionário.");
            if (forma == null) throw new RegraNegocioException("Selecione uma forma de pagamento.");
            financeiroService.registrarPagamento(
                    venda.getIdVenda(),
                    funcionario.getIdFuncionario(),
                    forma.getIdFormaPagamento(),
                    Formatador.parseMoeda(valueField.getText(), "Valor"),
                    Formatador.parseData(dateField.getText(), "Data do pagamento"),
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
        titleLabel = new JLabel("Registrar pagamento");
        formPanel = new JPanel(new GridLayout(7, 2, 12, 12));
        clientLabel = new JLabel("Cliente"); clientValue = new JLabel("-");
        debtLabel = new JLabel("Dívida"); debtValue = new JLabel("-");
        employeeLabel = new JLabel("Funcionário *"); employeeCombo = new JComboBox<>();
        paymentMethodLabel = new JLabel("Forma de pagamento *"); paymentMethodCombo = new JComboBox<>();
        valueLabel = new JLabel("Valor *"); valueField = new JTextField();
        dateLabel = new JLabel("Data *"); dateField = new JTextField();
        observationLabel = new JLabel("Observação"); observationField = new JTextField();
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cancelButton = new JButton("Cancelar"); saveButton = new JButton("Confirmar pagamento");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        formPanel.add(clientLabel); formPanel.add(clientValue);
        formPanel.add(debtLabel); formPanel.add(debtValue);
        formPanel.add(employeeLabel); formPanel.add(employeeCombo);
        formPanel.add(paymentMethodLabel); formPanel.add(paymentMethodCombo);
        formPanel.add(valueLabel); formPanel.add(valueField);
        formPanel.add(dateLabel); formPanel.add(dateField);
        formPanel.add(observationLabel); formPanel.add(observationField);
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
    private JLabel clientLabel;
    private JLabel clientValue;
    private JLabel debtLabel;
    private JLabel debtValue;
    private JLabel employeeLabel;
    private JComboBox<Funcionario> employeeCombo;
    private JLabel paymentMethodLabel;
    private JComboBox<FormaPagamento> paymentMethodCombo;
    private JLabel valueLabel;
    private JTextField valueField;
    private JLabel dateLabel;
    private JTextField dateField;
    private JLabel observationLabel;
    private JTextField observationField;
    private JPanel actionsPanel;
    private JButton cancelButton;
    private JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
