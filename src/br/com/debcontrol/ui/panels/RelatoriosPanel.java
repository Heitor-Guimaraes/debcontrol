package br.com.debcontrol.ui.panels;

import br.com.debcontrol.dto.ClienteResumo;
import br.com.debcontrol.service.RelatorioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;

public class RelatoriosPanel extends JPanel {
    private final RelatorioService service;
    private final NonEditableTableModel model = new NonEditableTableModel(
            new Object[]{"Cliente", "Telefone", "Saldo devedor", "Atraso"});

    /** Construtor usado exclusivamente pela aba Design do NetBeans. */
    public RelatoriosPanel() {
        this.service = null;
        initComponents();
        configurar();
    }

    public RelatoriosPanel(RelatorioService service) {
        this.service = service;
        initComponents(); configurar();
    }

    public void atualizar() {
        try {
            model.setRowCount(0);
            for (ClienteResumo item : service.clientesDevedores()) model.addRow(new Object[]{item.nome(), item.telefone(),
                    Formatador.moeda(item.saldoDevedor()), item.possuiAtraso() ? "Sim" : "Não"});
            calcularPeriodo();
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void configurar() {
        setBackground(UiTheme.BACKGROUND); setBorder(new EmptyBorder(24, 24, 24, 24));
        table.setModel(model); UiTheme.styleTable(table);
        startField.setText(Formatador.data(LocalDate.now().withDayOfMonth(1)));
        endField.setText(Formatador.data(LocalDate.now()));
        UiTheme.stylePrimaryButton(exportButton); UiTheme.styleSecondaryButton(calculateButton);
        calculateButton.addActionListener(event -> calcularPeriodo()); exportButton.addActionListener(event -> exportar());
    }

    private void calcularPeriodo() {
        try {
            var total = service.totalRecebido(Formatador.parseData(startField.getText(), "Data inicial"),
                    Formatador.parseData(endField.getText(), "Data final"));
            totalValue.setText(Formatador.moeda(total));
        } catch (RuntimeException exception) { MessageBox.error(this, exception); }
    }

    private void exportar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar clientes devedores");
        chooser.setSelectedFile(new File("clientes-devedores.csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo CSV", "csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) file = new File(file.getParentFile(), file.getName() + ".csv");
            try { service.exportarClientesDevedores(file.toPath()); MessageBox.success(this, "Relatório exportado com sucesso."); }
            catch (RuntimeException exception) { MessageBox.error(this, exception); }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        topPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel = new JPanel(new BorderLayout()); titleLabel = new JLabel("Relatórios"); exportButton = new JButton("Exportar CSV");
        periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); startField = new JTextField(10); endField = new JTextField(10);
        periodLabel = new JLabel("Período:"); toLabel = new JLabel("até");
        calculateButton = new JButton("Calcular"); totalLabel = new JLabel("Total recebido:"); totalValue = new JLabel("R$ 0,00");
        tableScroll = new JScrollPane(); table = new JTable();
        setLayout(new BorderLayout(0, 16)); titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerPanel.add(titleLabel, BorderLayout.WEST); headerPanel.add(exportButton, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        periodPanel.add(periodLabel); periodPanel.add(startField); periodPanel.add(toLabel); periodPanel.add(endField);
        periodPanel.add(calculateButton); periodPanel.add(Box.createHorizontalStrut(20)); periodPanel.add(totalLabel);
        totalValue.setFont(new Font("SansSerif", Font.BOLD, 18)); periodPanel.add(totalValue);
        topPanel.add(periodPanel, BorderLayout.SOUTH); add(topPanel, BorderLayout.NORTH);
        tableScroll.setViewportView(table); add(tableScroll, BorderLayout.CENTER);
        setBackground(new java.awt.Color(238, 244, 252));
        topPanel.setBackground(new java.awt.Color(238, 244, 252));
        headerPanel.setBackground(new java.awt.Color(238, 244, 252));
        titleLabel.setForeground(new java.awt.Color(30, 64, 175));
        exportButton.setBackground(new java.awt.Color(29, 78, 216));
        exportButton.setForeground(new java.awt.Color(255, 255, 255));
        periodPanel.setBackground(new java.awt.Color(221, 232, 249));
        calculateButton.setBackground(new java.awt.Color(229, 237, 252));
        calculateButton.setForeground(new java.awt.Color(30, 64, 175));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel topPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JButton exportButton;
    private JPanel periodPanel;
    private JLabel periodLabel;
    private JTextField startField;
    private JLabel toLabel;
    private JTextField endField;
    private JButton calculateButton;
    private JLabel totalLabel;
    private JLabel totalValue;
    private JScrollPane tableScroll;
    private JTable table;
    // End of variables declaration//GEN-END:variables
}
