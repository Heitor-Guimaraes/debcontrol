package br.com.debcontrol.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import br.com.debcontrol.ui.components.ScrollablePanel;
import java.awt.*;

public final class UiTheme {
    public static final Color BACKGROUND = new Color(238, 244, 252);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SIDEBAR = new Color(23, 43, 77);
    public static final Color PRIMARY = new Color(29, 78, 216);
    public static final Color PRIMARY_DARK = new Color(30, 64, 175);
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color WARNING = new Color(217, 119, 6);
    public static final Color DANGER = new Color(220, 38, 38);

    private UiTheme() {
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", new Color(219, 234, 254));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(171, 190, 217)), new EmptyBorder(6, 9, 6, 9)));
        UIManager.put("PasswordField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TextField.selectionBackground", new Color(210, 226, 253));
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("TextArea.background", SURFACE);
        UIManager.put("ScrollPane.background", SURFACE);
        UIManager.put("OptionPane.messageFont", new Font("SansSerif", Font.PLAIN, 14));
    }

    public static void styleTable(JTable table) {
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setRowHeight(34);
        table.setSelectionBackground(new Color(210, 226, 253));
        table.setSelectionForeground(TEXT);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable owner, Object value, boolean selected,
                                                           boolean focused, int row, int column) {
                super.getTableCellRendererComponent(owner, value, selected, focused, row, column);
                if (!selected) {
                    setBackground(row % 2 == 0 ? SURFACE : new Color(246, 249, 254));
                    setForeground(TEXT);
                }
                setBorder(focused ? BorderFactory.createLineBorder(PRIMARY) : new EmptyBorder(1, 10, 1, 10));
                setToolTipText(value == null ? null : value.toString());
                return this;
            }
        });
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(226, 236, 252));
        header.setForeground(PRIMARY_DARK);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
    }

    public static void stylePrimaryButton(JButton button) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setOpaque(true);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(true);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setOpaque(true);
        button.setBackground(new Color(229, 237, 252));
        button.setForeground(PRIMARY_DARK);
        button.setFocusPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(9, 14, 9, 14)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void configureDialog(JDialog dialog, JButton primaryAction) {
        dialog.getRootPane().setDefaultButton(primaryAction);
        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close-dialog");
        dialog.getRootPane().getActionMap().put("close-dialog", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                dialog.dispose();
            }
        });
        describeFields(dialog.getContentPane());
        Dimension preferred = dialog.getSize();
        JComponent content = (JComponent) dialog.getContentPane();
        JScrollPane scroll = new JScrollPane(new ScrollablePanel(content,
                Math.max(480, content.getPreferredSize().width), content.getPreferredSize().height));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        dialog.setContentPane(scroll);
        dialog.setResizable(true);
        WindowSizing.fit(dialog, preferred, new Dimension(400, 300));
    }

    private static void describeFields(Container parent) {
        Component[] components = parent.getComponents();
        for (int index = 0; index < components.length; index++) {
            Component current = components[index];
            if (current instanceof JLabel label && index + 1 < components.length) {
                Component next = components[index + 1];
                if (next instanceof JTextField || next instanceof JComboBox<?> || next instanceof JScrollPane) {
                    label.setLabelFor(next);
                    if (next instanceof JComponent field) {
                        field.getAccessibleContext().setAccessibleName(label.getText().replace(" *", ""));
                        String name = label.getText().toLowerCase(java.util.Locale.ROOT);
                        if (name.contains("data") || name.contains("vencimento") || name.contains("contato")) {
                            if (next instanceof JTextField) field.setToolTipText("Data no formato dd/MM/aaaa");
                        } else if (name.contains("valor")) {
                            field.setToolTipText("Valor em reais, por exemplo: 150,00");
                        } else if (label.getText().contains("*")) {
                            field.setToolTipText("Campo obrigatório");
                        }
                    }
                }
            }
            if (current instanceof Container child) describeFields(child);
        }
    }

    public static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }
}
