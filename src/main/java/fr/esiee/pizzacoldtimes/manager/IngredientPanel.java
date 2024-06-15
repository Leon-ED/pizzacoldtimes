package fr.esiee.pizzacoldtimes.manager;

import fr.esiee.pizzacoldtimes.database.DatabaseConnection;
import fr.esiee.pizzacoldtimes.utils.CustomTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

class IngredientPanel extends JPanel {
    private JTable ingredientTable;
    private JTextField nameField;
    private JButton addButton, modifyButton, deleteButton, searchButton;

    public IngredientPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(1, 4));
        nameField = new JTextField();

        addButton = new JButton("Ajouter Ingrédient");
        modifyButton = new JButton("Modifier Ingrédient");
        deleteButton = new JButton("Supprimer Ingrédient");
        searchButton = new JButton("Rechercher Ingrédient");

        addButton.addActionListener(e -> addIngredient());
        modifyButton.addActionListener(e -> modifyIngredient());
        deleteButton.addActionListener(e -> deleteIngredient());
        searchButton.addActionListener(e -> searchIngredients());

        inputPanel.add(new JLabel("Nom:"));
        inputPanel.add(nameField);
        inputPanel.add(addButton);
        inputPanel.add(modifyButton);

        JPanel controlPanel = new JPanel(new GridLayout(1, 2));
        controlPanel.add(deleteButton);
        controlPanel.add(searchButton);

        ingredientTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(ingredientTable);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        loadIngredients();
    }

    public void loadIngredients() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ingredient");
            Vector<String> columnNames = new Vector<>();
            columnNames.add("Nom");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nom"));
                data.add(row);
            }
            ingredientTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addIngredient() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showInvalidNamePopup();
            return;
        }
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO ingredient (nom) VALUES (?)");
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            loadIngredients();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow != -1) {
            String oldName = (String) ingredientTable.getValueAt(selectedRow, 0);
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                showInvalidNamePopup();
                return;
            }
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("UPDATE ingredient SET nom = ? WHERE nom = ?");
                pstmt.setString(1, newName);
                pstmt.setString(2, oldName);
                pstmt.executeUpdate();
                loadIngredients();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) ingredientTable.getValueAt(selectedRow, 0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ingredient WHERE nom = ?");
                pstmt.setString(1, name);
                pstmt.executeUpdate();
                loadIngredients();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchIngredients() {
        String keyword = nameField.getText().trim();
        if (keyword.isEmpty()) {
            showInvalidNamePopup();
            return;
        }
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ingredient WHERE nom LIKE ?");
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Nom");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nom"));
                data.add(row);
            }

            ingredientTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showInvalidNamePopup() {
        JOptionPane.showMessageDialog(this, "Nom invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}


