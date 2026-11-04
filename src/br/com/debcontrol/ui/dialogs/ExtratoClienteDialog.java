package br.com.debcontrol.ui.dialogs;

import br.com.debcontrol.model.Cliente;
import br.com.debcontrol.model.VendaFiada;
import br.com.debcontrol.service.FinanceiroService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ExtratoClienteDialog extends JDialog {
    private final NonEditableTableModel model = new NonEditableTableModel(
            new Object[]{"Venda", "Data", "Vencimento", "Valor", "Pago", "Saldo", "Situação"});

    /** Construtor usado exclusivamente pela aba Design do NetBeans. */
    public ExtratoClienteDialog() {
        initComponents();
    }

    public ExtratoClienteDialog(Window owner, Cliente cliente, FinanceiroService service) {
        super(owner, "Extrato do cliente", ModalityType.APPLICATION_MODAL);
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(880, 520));
        setLocationRelativeTo(owner);
        rootPanel.setBorder(new EmptyBorder(22, 22, 22, 22));
        clientValue.setText(cliente.getNome());
        var saldo = service.saldoCliente(cliente.getIdCliente());
        balanceValue.setText(Formatador.moeda(saldo));
        statusValue.setText(saldo.signum() > 0 ? "DEVENDO" : "SEM DÍVIDA");
        table.setModel(model); UiTheme.styleTable(table);
        for (VendaFiada venda : service.extratoCliente(cliente.getIdCliente())) {
            String situacao = venda.isAtrasada() ? "ATRASADA" : venda.getStatus().name();
            model.addRow(new Object[]{venda.getIdVenda(), Formatador.data(venda.getDataVenda()),
                    Formatador.data(venda.getDataVencimento()), Formatador.moeda(venda.getValorTotal()),
                    Formatador.moeda(venda.getValorPago()), Formatador.moeda(venda.getSaldo()), situacao});
        }
        UiTheme.styleSecondaryButton(closeButton);
        closeButton.addActionListener(event -> dispose());
        UiTheme.configureDialog(this, closeButton);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        rootPanel = new JPanel(new BorderLayout(0, 16));
        headerPanel = new JPanel(new GridLayout(2, 3, 12, 6));
        clientLabel = new JLabel("Cliente"); balanceLabel = new JLabel("Saldo devedor"); statusLabel = new JLabel("Situação");
        clientValue = new JLabel("—"); balanceValue = new JLabel("R$ 0,00"); statusValue = new JLabel("—");
        tableScroll = new JScrollPane(); table = new JTable();
        actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); closeButton = new JButton("Fechar");
        headerPanel.add(clientLabel); headerPanel.add(balanceLabel); headerPanel.add(statusLabel);
        headerPanel.add(clientValue); headerPanel.add(balanceValue); headerPanel.add(statusValue);
        rootPanel.add(headerPanel, BorderLayout.NORTH); tableScroll.setViewportView(table);
        rootPanel.add(tableScroll, BorderLayout.CENTER); actionsPanel.add(closeButton); rootPanel.add(actionsPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel); pack();
        rootPanel.setBackground(new java.awt.Color(255, 255, 255));
        headerPanel.setBackground(new java.awt.Color(238, 244, 252));
        actionsPanel.setBackground(new java.awt.Color(255, 255, 255));
        closeButton.setBackground(new java.awt.Color(229, 237, 252));
        closeButton.setForeground(new java.awt.Color(30, 64, 175));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel rootPanel;
    private JPanel headerPanel;
    private JLabel clientLabel;
    private JLabel balanceLabel;
    private JLabel statusLabel;
    private JLabel clientValue;
    private JLabel balanceValue;
    private JLabel statusValue;
    private JScrollPane tableScroll;
    private JTable table;
    private JPanel actionsPanel;
    private JButton closeButton;
    // End of variables declaration//GEN-END:variables
}
