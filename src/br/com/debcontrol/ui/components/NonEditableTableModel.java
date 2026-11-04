package br.com.debcontrol.ui.components;

import javax.swing.table.DefaultTableModel;

public class NonEditableTableModel extends DefaultTableModel {
    public NonEditableTableModel(Object[] columns) {
        super(columns, 0);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
