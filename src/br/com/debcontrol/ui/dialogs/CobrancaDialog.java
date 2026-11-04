package br.com.debcontrol.ui.dialogs;

import br.com.debcontrol.exception.RegraNegocioException;
import br.com.debcontrol.model.Funcionario;
import br.com.debcontrol.model.MeioContato;
import br.com.debcontrol.model.VendaFiada;
import br.com.debcontrol.service.CobrancaService;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.service.FuncionarioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class CobrancaDialog extends JDialog {
    private final CobrancaService cobrancaService;
    private final FinanceiroService financeiroService;
    private final FuncionarioService funcionarioService;
    private boolean salvo;

    public CobrancaDialog() {
        this.cobrancaService = null;
        this.financeiroService = null;
        this.funcionarioService = null;
        initComponents();
        configurar();
    }

    public CobrancaDialog(Window owner, CobrancaService cobrancaService,
                          FinanceiroService financeiroService, FuncionarioService funcionarioService) {
        super(owner, "Registrar cobrança", ModalityType.APPLICATION_MODAL);
        this.cobrancaService = cobrancaService;
        this.financeiroService = financeiroService;
        this.funcionarioService = funcionarioService;
        initComponents();
        configurar();
        carregarOpcoes();
    }

    public boolean isSalvo() { return salvo; }

    private void configurar() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(620, 520));
        setLocationRelativeTo(getOwner());
        rootPanel.setBorder(new EmptyBorder(22, 22, 22, 22));
        dateField.setText(Formatador.data(LocalDate.now()));
        contactMethodCombo.setModel(new DefaultComboBoxModel<>(MeioContato.values()));
        UiTheme.stylePrimaryButton(saveButton);
        UiTheme.configureDialog(this, saveButton);
        UiTheme.styleSecondaryButton(cancelButton);
        saveButton.addActionListener(event -> salvar());
        cancelButton.addActionListener(event -> dispose());
    }

    private void carregarOpcoes() {
        DefaultComboBoxModel<VendaFiada> vendas = new DefaultComboBoxModel<>();
        for (VendaFiada venda : financeiroService.listarVendas("", true)) vendas.addElement(venda);
        saleCombo.setModel(vendas);
        saleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                if (value instanceof VendaFiada venda) {
                    value = "#" + venda.getIdVenda() + " - " + venda.getClienteNome()
                            + " - " + Formatador.moeda(venda.getSaldo());
                }
                return super.getListCellRendererComponent(list, value, index, selected, focus);
            }
        });
        DefaultComboBoxModel<Funcionario> funcionarios = new DefaultComboBoxModel<>();
        for (Funcionario funcionario : funcionarioService.listar("", true)) funcionarios.addElement(funcionario);
        employeeCombo.setModel(funcionarios);
    }

    private void salvar() {
        try {
            VendaFiada venda = (VendaFiada) saleCombo.getSelectedItem();
            Funcionario funcionario = (Funcionario) employeeCombo.getSelectedItem();
            if (venda == null) throw new RegraNegocioException("Selecione uma venda pendente.");
            if (funcionario == null) throw new RegraNegocioException("Selecione um funcionário.");
            LocalDate proximo = nextContactField.getText().isBlank()
                    ? null : Formatador.parseData(nextContactField.getText(), "Próximo contato");
            cobrancaService.registrar(
                    venda.getIdVenda(),
                    funcionario.getIdFuncionario(),
                    Formatador.parseData(dateField.getText(), "Data da cobrança"),
                    (MeioContato) contactMethodCombo.getSelectedItem(),
                    resultField.getText(),
                    proximo,
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
        titleLabel = new JLabel("Registrar cobrança");
        formPanel = new JPanel(new GridLayout(7, 2, 12, 12));
        saleLabel = new JLabel("Venda pendente *"); saleCombo = new JComboBox<>();
        employeeLabel = new JLabel("Funcionário *"); employeeCombo = new JComboBox<>();
        dateLabel = new JLabel("Data *"); dateField = new JTextField();
        contactMethodLabel = new JLabel("Meio de contato *"); contactMethodCombo = new JComboBox<>();
        resultLabel = new JLabel("Resultado *"); resultField = new JTextField();
        nextContactLabel = new JLabel("Próximo contato"); nextContactField = new JTextField();
        observationLabel = new JLabel("Observação"); observationField = new JTextField();
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cancelButton = new JButton("Cancelar"); saveButton = new JButton("Registrar cobrança");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        formPanel.add(saleLabel); formPanel.add(saleCombo);
        formPanel.add(employeeLabel); formPanel.add(employeeCombo);
        formPanel.add(dateLabel); formPanel.add(dateField);
        formPanel.add(contactMethodLabel); formPanel.add(contactMethodCombo);
        formPanel.add(resultLabel); formPanel.add(resultField);
        formPanel.add(nextContactLabel); formPanel.add(nextContactField);
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
    private JLabel saleLabel;
    private JComboBox<VendaFiada> saleCombo;
    private JLabel employeeLabel;
    private JComboBox<Funcionario> employeeCombo;
    private JLabel dateLabel;
    private JTextField dateField;
    private JLabel contactMethodLabel;
    private JComboBox<MeioContato> contactMethodCombo;
    private JLabel resultLabel;
    private JTextField resultField;
    private JLabel nextContactLabel;
    private JTextField nextContactField;
    private JLabel observationLabel;
    private JTextField observationField;
    private JPanel actionsPanel;
    private JButton cancelButton;
    private JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
