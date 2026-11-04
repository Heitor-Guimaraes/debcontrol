package br.com.debcontrol.ui.panels;

import br.com.debcontrol.dto.ClienteResumo;
import br.com.debcontrol.dto.DashboardResumo;
import br.com.debcontrol.service.RelatorioService;
import br.com.debcontrol.ui.UiTheme;
import br.com.debcontrol.ui.components.MessageBox;
import br.com.debcontrol.ui.components.NonEditableTableModel;
import br.com.debcontrol.util.Formatador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private final RelatorioService relatorioService;
    private final NonEditableTableModel tableModel = new NonEditableTableModel(
            new Object[]{"Cliente", "Telefone", "Saldo devedor", "Situação"});

    /** Construtor usado exclusivamente pela aba Design do NetBeans. */
    public DashboardPanel() {
        this.relatorioService = null;
        initComponents();
        configurarVisual();
    }

    public DashboardPanel(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
        initComponents();
        configurarVisual();
    }

    public void atualizar() {
        try {
            DashboardResumo resumo = relatorioService.dashboard();
            totalReceberValue.setText(Formatador.moeda(resumo.totalAReceber()));
            recebidoMesValue.setText(Formatador.moeda(resumo.totalRecebidoMes()));
            clientesValue.setText(String.valueOf(resumo.clientesDevedores()));
            atrasadasValue.setText(String.valueOf(resumo.dividasAtrasadas()));
            tableModel.setRowCount(0);
            for (ClienteResumo item : relatorioService.clientesDevedores()) {
                tableModel.addRow(new Object[]{item.nome(), item.telefone(), Formatador.moeda(item.saldoDevedor()),
                        item.possuiAtraso() ? "Atrasado" : "Em dia"});
            }
        } catch (RuntimeException exception) {
            MessageBox.error(this, exception);
        }
    }

    private void configurarVisual() {
        setBackground(UiTheme.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        titleLabel.setForeground(UiTheme.TEXT);
        subtitleLabel.setForeground(UiTheme.MUTED);
        configurarCard(totalReceberCard, totalReceberLabel, totalReceberValue);
        configurarCard(recebidoMesCard, recebidoMesLabel, recebidoMesValue);
        configurarCard(clientesCard, clientesLabel, clientesValue);
        configurarCard(atrasadasCard, atrasadasLabel, atrasadasValue);
        totalReceberCard.setBackground(new Color(232, 240, 255));
        totalReceberValue.setForeground(UiTheme.PRIMARY_DARK);
        recebidoMesCard.setBackground(new Color(231, 247, 239));
        recebidoMesValue.setForeground(new Color(21, 108, 61));
        clientesCard.setBackground(new Color(242, 235, 255));
        clientesValue.setForeground(new Color(100, 47, 163));
        atrasadasCard.setBackground(new Color(255, 242, 223));
        atrasadasValue.setForeground(new Color(146, 72, 7));
        table.setModel(tableModel);
        UiTheme.styleTable(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
    }

    private void configurarCard(JPanel card, JLabel label, JLabel value) {
        card.setBackground(UiTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER), new EmptyBorder(16, 16, 16, 16)));
        label.setForeground(UiTheme.MUTED);
        value.setForeground(UiTheme.TEXT);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        headerPanel = new JPanel();
        titleLabel = new JLabel();
        subtitleLabel = new JLabel();
        metricsPanel = new JPanel();
        totalReceberCard = new JPanel();
        totalReceberLabel = new JLabel();
        totalReceberValue = new JLabel();
        recebidoMesCard = new JPanel();
        recebidoMesLabel = new JLabel();
        recebidoMesValue = new JLabel();
        clientesCard = new JPanel();
        clientesLabel = new JLabel();
        clientesValue = new JLabel();
        atrasadasCard = new JPanel();
        atrasadasLabel = new JLabel();
        atrasadasValue = new JLabel();
        centerPanel = new JPanel();
        tableScroll = new JScrollPane();
        table = new JTable();

        setLayout(new BorderLayout(0, 18));

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setText("Visão geral");
        headerPanel.add(titleLabel);
        subtitleLabel.setText("Acompanhe os valores do fiado e as pendências do mercado.");
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        metricsPanel.setLayout(new GridLayout(1, 4, 12, 0));
        totalReceberCard.setLayout(new BorderLayout());
        totalReceberLabel.setText("TOTAL A RECEBER");
        totalReceberCard.add(totalReceberLabel, BorderLayout.NORTH);
        totalReceberValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        totalReceberValue.setText("R$ 0,00");
        totalReceberCard.add(totalReceberValue, BorderLayout.CENTER);
        metricsPanel.add(totalReceberCard);

        recebidoMesCard.setLayout(new BorderLayout());
        recebidoMesLabel.setText("RECEBIDO NO MÊS");
        recebidoMesCard.add(recebidoMesLabel, BorderLayout.NORTH);
        recebidoMesValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        recebidoMesValue.setText("R$ 0,00");
        recebidoMesCard.add(recebidoMesValue, BorderLayout.CENTER);
        metricsPanel.add(recebidoMesCard);

        clientesCard.setLayout(new BorderLayout());
        clientesLabel.setText("CLIENTES DEVENDO");
        clientesCard.add(clientesLabel, BorderLayout.NORTH);
        clientesValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        clientesValue.setText("0");
        clientesCard.add(clientesValue, BorderLayout.CENTER);
        metricsPanel.add(clientesCard);

        atrasadasCard.setLayout(new BorderLayout());
        atrasadasLabel.setText("DÍVIDAS ATRASADAS");
        atrasadasCard.add(atrasadasLabel, BorderLayout.NORTH);
        atrasadasValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        atrasadasValue.setText("0");
        atrasadasCard.add(atrasadasValue, BorderLayout.CENTER);
        metricsPanel.add(atrasadasCard);

        centerPanel.setLayout(new BorderLayout(0, 18));
        centerPanel.add(metricsPanel, BorderLayout.NORTH);
        table.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{},
                new String[]{"Cliente", "Telefone", "Saldo devedor", "Situação"}));
        tableScroll.setViewportView(table);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        setBackground(new java.awt.Color(238, 244, 252));
        headerPanel.setBackground(new java.awt.Color(238, 244, 252));
        titleLabel.setForeground(new java.awt.Color(30, 64, 175));
        centerPanel.setBackground(new java.awt.Color(238, 244, 252));
        metricsPanel.setBackground(new java.awt.Color(238, 244, 252));
        totalReceberCard.setBackground(new java.awt.Color(232, 240, 255));
        recebidoMesCard.setBackground(new java.awt.Color(231, 247, 239));
        clientesCard.setBackground(new java.awt.Color(242, 235, 255));
        atrasadasCard.setBackground(new java.awt.Color(255, 242, 223));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel metricsPanel;
    private JPanel totalReceberCard;
    private JLabel totalReceberLabel;
    private JLabel totalReceberValue;
    private JPanel recebidoMesCard;
    private JLabel recebidoMesLabel;
    private JLabel recebidoMesValue;
    private JPanel clientesCard;
    private JLabel clientesLabel;
    private JLabel clientesValue;
    private JPanel atrasadasCard;
    private JLabel atrasadasLabel;
    private JLabel atrasadasValue;
    private JPanel centerPanel;
    private JScrollPane tableScroll;
    private JTable table;
    // End of variables declaration//GEN-END:variables
}
