package fr.esiee.pizzacoldtimes.manager;

import fr.esiee.pizzacoldtimes.database.DatabaseConnection;
import fr.esiee.pizzacoldtimes.utils.CustomTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

class VehiculePanel extends JPanel {
    private JTable vehiculeTable;
    private JTextField immatriculationField, modeleField, marqueField;
    private JComboBox<String> typeComboBox;
    private JButton addButton, modifyButton, deleteButton, searchButton;

    public VehiculePanel() {
        setLayout(new BorderLayout());

        immatriculationField = new JTextField(10);
        typeComboBox = new JComboBox<>(new String[]{"MOTO", "VOITURE"});
        modeleField = new JTextField(20);
        marqueField = new JTextField(20);

        addButton = new JButton("Ajouter Véhicule");
        modifyButton = new JButton("Modifier Véhicule");
        deleteButton = new JButton("Supprimer Véhicule");
        searchButton = new JButton("Rechercher Véhicule");

        addButton.addActionListener(e -> addVehicule());
        modifyButton.addActionListener(e -> modifyVehicule());
        deleteButton.addActionListener(e -> deleteVehicule());
        searchButton.addActionListener(e -> searchVehicules());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Immatriculation:"));
        inputPanel.add(immatriculationField);
        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeComboBox);
        inputPanel.add(new JLabel("Modèle:"));
        inputPanel.add(modeleField);
        inputPanel.add(new JLabel("Marque:"));
        inputPanel.add(marqueField);
        inputPanel.add(addButton);
        inputPanel.add(modifyButton);

        JPanel controlPanel = new JPanel(new GridLayout(1, 2));
        controlPanel.add(deleteButton);
        controlPanel.add(searchButton);

        vehiculeTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(vehiculeTable);

        add(inputPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        loadVehicules();
    }

    private void loadVehicules() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM vehicule");

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Immatriculation");
            columnNames.add("Type");
            columnNames.add("Modèle");
            columnNames.add("Marque");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("immatriculation"));
                row.add(rs.getString("type"));
                row.add(rs.getString("modele"));
                row.add(rs.getString("marque"));
                data.add(row);
            }

            vehiculeTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addVehicule() {
        String immatriculation = immatriculationField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        String modele = modeleField.getText();
        String marque = marqueField.getText();

        if (immatriculation.isEmpty() || type.isEmpty() || modele.isEmpty() || marque.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO vehicule (immatriculation, type, modele, marque) VALUES (?, ?, ?, ?)");
            pstmt.setString(1, immatriculation);
            pstmt.setString(2, type);
            pstmt.setString(3, modele);
            pstmt.setString(4, marque);
            pstmt.executeUpdate();
            loadVehicules();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyVehicule() {
        int selectedRow = vehiculeTable.getSelectedRow();
        if (selectedRow != -1) {
            String immatriculation = (String) vehiculeTable.getValueAt(selectedRow, 0);
            String type = (String) typeComboBox.getSelectedItem();
            String modele = modeleField.getText();
            String marque = marqueField.getText();

            if (immatriculation.isEmpty() || type.isEmpty() || modele.isEmpty() || marque.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("UPDATE vehicule SET type = ?, modele = ?, marque = ? WHERE immatriculation = ?");
                pstmt.setString(1, type);
                pstmt.setString(2, modele);
                pstmt.setString(3, marque);
                pstmt.setString(4, immatriculation);
                pstmt.executeUpdate();
                loadVehicules();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteVehicule() {
        int selectedRow = vehiculeTable.getSelectedRow();
        if (selectedRow != -1) {
            String immatriculation = (String) vehiculeTable.getValueAt(selectedRow, 0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM vehicule WHERE immatriculation = ?");
                pstmt.setString(1, immatriculation);
                pstmt.executeUpdate();
                loadVehicules();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchVehicules() {
        String keyword = immatriculationField.getText();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM vehicule WHERE immatriculation LIKE ? OR type LIKE ? OR modele LIKE ? OR marque LIKE ?");
            String searchQuery = "%" + keyword + "%";
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);
            pstmt.setString(3, searchQuery);
            pstmt.setString(4, searchQuery);
            ResultSet rs = pstmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Immatriculation");
            columnNames.add("Type");
            columnNames.add("Modèle");
            columnNames.add("Marque");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("immatriculation"));
                row.add(rs.getString("type"));
                row.add(rs.getString("modele"));
                row.add(rs.getString("marque"));
                data.add(row);
            }

            vehiculeTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
