package fr.esiee;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

class ClientPanel extends JPanel {
    private JTable clientTable;
    private JTextField searchField;
    private JButton searchButton, commandesButton;

    public ClientPanel() {
        setLayout(new BorderLayout());

        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> searchClients());

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Recherche Client:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        clientTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(clientTable);

        commandesButton = new JButton("Voir Commandes");
        commandesButton.addActionListener(e -> openCommandesPanel());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(commandesButton);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadClients();
    }

    private void loadClients() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM client");

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Prénom");
            columnNames.add("Nom");
            columnNames.add("Email");
            columnNames.add("Adresse");
            columnNames.add("Crédit");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("idClient"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("email"));
                row.add(rs.getString("adresse"));
                row.add(rs.getInt("credit"));
                data.add(row);
            }

            clientTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchClients() {
        String keyword = searchField.getText();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM client WHERE prenom LIKE ? OR nom LIKE ? OR email LIKE ? OR adresse LIKE ?");
            String searchQuery = "%" + keyword + "%";
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);
            pstmt.setString(3, searchQuery);
            pstmt.setString(4, searchQuery);
            ResultSet rs = pstmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Prénom");
            columnNames.add("Nom");
            columnNames.add("Email");
            columnNames.add("Adresse");
            columnNames.add("Crédit");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("idClient"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("email"));
                row.add(rs.getString("adresse"));
                row.add(rs.getInt("credit"));
                data.add(row);
            }

            clientTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openCommandesPanel() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1) {
            int clientId = (int) clientTable.getValueAt(selectedRow, 0);
            new OrdersList(clientId);
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class CustomTableModel extends javax.swing.table.DefaultTableModel {
    CustomTableModel(Vector<Vector<Object>> data, Vector<String> columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // All cells are not editable
    }
}
