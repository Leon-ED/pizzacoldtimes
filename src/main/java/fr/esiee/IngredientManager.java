package fr.esiee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;

public class IngredientManager extends JFrame {
    private JCheckBox[] ingredientCheckboxes;
    private JButton saveButton;
    private String pizzaName;
    private JTextField searchField;
    private JPanel checkboxPanel;
    private ArrayList<JCheckBox> allCheckboxes;

    public IngredientManager(String pizzaName, Runnable onSaveCallback) {
        this.pizzaName = pizzaName;
        setTitle("Gérer les Ingrédients pour " + pizzaName);
        setSize(400, 300);
        setLayout(new BorderLayout());

        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterIngredients();
            }
        });

        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ingredient");

            allCheckboxes = new ArrayList<>();
            while (rs.next()) {
                String ingredientName = rs.getString("nom");
                JCheckBox checkBox = new JCheckBox(ingredientName);
                allCheckboxes.add(checkBox);
                checkboxPanel.add(checkBox);
            }
            ingredientCheckboxes = allCheckboxes.toArray(new JCheckBox[0]);

            PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM pizza_ingredients WHERE nomPizza = ?");
            pstmt.setString(1, pizzaName);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String ingredientName = rs.getString("nom");
                for (JCheckBox checkBox : ingredientCheckboxes) {
                    if (checkBox.getText().equals(ingredientName)) {
                        checkBox.setSelected(true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            saveIngredients();
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
        });

        add(searchField, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void filterIngredients() {
        String filterText = searchField.getText().toLowerCase();
        checkboxPanel.removeAll();
        for (JCheckBox checkBox : allCheckboxes) {
            if (checkBox.getText().toLowerCase().contains(filterText)) {
                checkboxPanel.add(checkBox);
            }
        }
        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    private void saveIngredients() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM pizza_ingredients WHERE nomPizza = ?");
            deleteStmt.setString(1, pizzaName);
            deleteStmt.executeUpdate();

            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO pizza_ingredients (nomPizza, nom) VALUES (?, ?)");
            for (JCheckBox checkBox : ingredientCheckboxes) {
                if (checkBox.isSelected()) {
                    insertStmt.setString(1, pizzaName);
                    insertStmt.setString(2, checkBox.getText());
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dispose();
    }
}
