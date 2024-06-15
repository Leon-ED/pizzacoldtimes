package fr.esiee.shared;
import java.util.Vector;

public class CustomTableModel extends javax.swing.table.DefaultTableModel {
    public CustomTableModel(Vector<Vector<Object>> data, Vector<String> columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // All cells are not editable
    }
}