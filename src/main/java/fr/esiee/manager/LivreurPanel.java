package fr.esiee.manager;

import fr.esiee.shared.CustomTableModel;
import fr.esiee.shared.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

class LivreurPanel extends JPanel {
    private JTable livreurTable;
    private JTextField prenomField, nomField;
    private JButton addButton, modifyButton, deleteButton, searchButton;

    public LivreurPanel() {
        setLayout(new BorderLayout());

        prenomField = new JTextField(20);
        nomField = new JTextField(20);

        addButton = new JButton("Ajouter Livreur");
        modifyButton = new JButton("Modifier Livreur");
        deleteButton = new JButton("Supprimer Livreur");
        searchButton = new JButton("Rechercher Livreur");

        addButton.addActionListener(e -> addLivreur());
        modifyButton.addActionListener(e -> modifyLivreur());
        deleteButton.addActionListener(e -> deleteLivreur());
        searchButton.addActionListener(e -> searchLivreurs());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Prénom:"));
        inputPanel.add(prenomField);
        inputPanel.add(new JLabel("Nom:"));
        inputPanel.add(nomField);
        inputPanel.add(addButton);
        inputPanel.add(modifyButton);

        JPanel controlPanel = new JPanel(new GridLayout(1, 2));
        controlPanel.add(deleteButton);
        controlPanel.add(searchButton);

        livreurTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(livreurTable);

        add(inputPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        loadLivreurs();
    }

    private void loadLivreurs() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM livreur");

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Matricule");
            columnNames.add("Prénom");
            columnNames.add("Nom");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("matricule"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("nom"));
                data.add(row);
            }

            livreurTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addLivreur() {
        String prenom = prenomField.getText();
        String nom = nomField.getText();

        if (prenom.isEmpty() || nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO livreur (prenom, nom) VALUES (?, ?)");
            pstmt.setString(1, prenom);
            pstmt.setString(2, nom);
            pstmt.executeUpdate();
            loadLivreurs();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyLivreur() {
        int selectedRow = livreurTable.getSelectedRow();
        if (selectedRow != -1) {
            int matricule = (int) livreurTable.getValueAt(selectedRow, 0);
            String prenom = prenomField.getText();
            String nom = nomField.getText();

            if (prenom.isEmpty() || nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("UPDATE livreur SET prenom = ?, nom = ? WHERE matricule = ?");
                pstmt.setString(1, prenom);
                pstmt.setString(2, nom);
                pstmt.setInt(3, matricule);
                pstmt.executeUpdate();
                loadLivreurs();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteLivreur() {
        int selectedRow = livreurTable.getSelectedRow();
        if (selectedRow != -1) {
            int matricule = (int) livreurTable.getValueAt(selectedRow, 0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM livreur WHERE matricule = ?");
                pstmt.setInt(1, matricule);
                pstmt.executeUpdate();
                loadLivreurs();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchLivreurs() {
        String keyword = prenomField.getText();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM livreur WHERE prenom LIKE ? OR nom LIKE ?");
            String searchQuery = "%" + keyword + "%";
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);
            ResultSet rs = pstmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Matricule");
            columnNames.add("Prénom");
            columnNames.add("Nom");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("matricule"));
                row.add(rs.getString("prenom"));
                row.add(rs.getString("nom"));
                data.add(row);
            }

            livreurTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
