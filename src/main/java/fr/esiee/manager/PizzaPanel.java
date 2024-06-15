package fr.esiee.manager;

import fr.esiee.shared.CustomTableModel;
import fr.esiee.shared.DatabaseConnection;
import utils.TextAreaRenderer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

class PizzaPanel extends JPanel {
    private JTable pizzaTable;
    private JTextField nameField, priceField;
    private JButton addButton, modifyButton, deleteButton, searchButton, ingredientButton;

    public PizzaPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 3));
        nameField = new JTextField();
        priceField = new JTextField();

        addButton = new JButton("Ajouter Pizza");
        modifyButton = new JButton("Modifier Pizza");
        deleteButton = new JButton("Supprimer Pizza");
        searchButton = new JButton("Rechercher Pizza");
        ingredientButton = new JButton("Gérer Ingrédients");

        addButton.addActionListener(e -> addPizza());
        modifyButton.addActionListener(e -> modifyPizza());
        deleteButton.addActionListener(e -> deletePizza());
        searchButton.addActionListener(e -> searchPizzas());
        ingredientButton.addActionListener(e -> manageIngredients());

        inputPanel.add(new JLabel("Nom:"));
        inputPanel.add(nameField);
        inputPanel.add(addButton);
        inputPanel.add(new JLabel("Prix:"));
        inputPanel.add(priceField);
        inputPanel.add(modifyButton);

        JPanel controlPanel = new JPanel(new GridLayout(1, 3));
        controlPanel.add(deleteButton);
        controlPanel.add(searchButton);
        controlPanel.add(ingredientButton);

        pizzaTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(pizzaTable);

        add(inputPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        loadPizzas();
    }

     public void loadPizzas() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pizza");

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Nom");
            columnNames.add("Prix");
            columnNames.add("Ingrédients");

            Vector<Vector<Object>> data = new Vector<>();
            Statement stmtIngredients = conn.createStatement();


            while (rs.next()) {
                ResultSet rsIngredients = stmtIngredients.executeQuery("SELECT * FROM pizza_ingredients WHERE nomPizza = '" + rs.getString("nomPizza") + "'");
                ArrayList<String> listIngredients = new ArrayList<>();
                while (rsIngredients.next()) {
                    listIngredients.add(rsIngredients.getString("nom"));
                }

                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nomPizza"));
                row.add(rs.getInt("prixBase"));
                row.add(String.join(" , ", listIngredients));

                data.add(row);
            }

            pizzaTable.setModel(new CustomTableModel(data, columnNames));
            pizzaTable.getColumnModel().getColumn(2).setCellRenderer(new TextAreaRenderer());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPizza() {
        String name = nameField.getText();
        int price = Integer.parseInt(priceField.getText());
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO pizza (nomPizza, prixBase) VALUES (?, ?)");
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.executeUpdate();
            loadPizzas();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyPizza() {
        int selectedRow = pizzaTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) pizzaTable.getValueAt(selectedRow, 0);
            int price = Integer.parseInt(priceField.getText());
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("UPDATE pizza SET prixBase = ? WHERE nomPizza = ?");
                pstmt.setInt(1, price);
                pstmt.setString(2, name);
                pstmt.executeUpdate();
                loadPizzas();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deletePizza() {
        int selectedRow = pizzaTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) pizzaTable.getValueAt(selectedRow, 0);
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pizza WHERE nomPizza = ?");
                pstmt.setString(1, name);
                pstmt.executeUpdate();
                loadPizzas();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchPizzas() {
        String keyword = nameField.getText();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM pizza WHERE nomPizza LIKE ?");
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("Nom");
            columnNames.add("Prix");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nomPizza"));
                row.add(rs.getInt("prixBase"));
                data.add(row);
            }

            pizzaTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void manageIngredients() {
        int selectedRow = pizzaTable.getSelectedRow();
        if (selectedRow != -1) {
            String pizzaName = (String) pizzaTable.getValueAt(selectedRow, 0);
            new IngredientManager(pizzaName, this::loadPizzas);
        }
    }
}





