package fr.esiee.client;

import fr.esiee.shared.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class OrderMenu {
    private JFrame frame;
    private JPanel panel;
    private JPanel pizzaListPanel;
    private ButtonGroup pizzaGroup;
    private ButtonGroup sizeGroup;
    private JLabel priceLabel;
    private Map<String, Integer> pizzaPrices;
    private Map<String, String> pizzaIngredients;
    private int clientId;
    private int solde;
    private boolean freePizzaAvailable;

    public OrderMenu(int clientId) {
        this.clientId = clientId;

        frame = new JFrame("Menu de Commande");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        pizzaGroup = new ButtonGroup();
        sizeGroup = new ButtonGroup();
        pizzaPrices = new HashMap<>();
        pizzaIngredients = new HashMap<>();

        // Size selection radio buttons
        JPanel sizePanel = new JPanel(new GridLayout(1, 3));
        JRadioButton sizeNaine = new JRadioButton("Naine");
        sizeNaine.setActionCommand("Naine");
        JRadioButton sizeHumaine = new JRadioButton("Humaine", true);
        sizeHumaine.setActionCommand("Humaine");
        JRadioButton sizeOgresse = new JRadioButton("Ogresse");
        sizeOgresse.setActionCommand("Ogresse");
        sizeGroup.add(sizeNaine);
        sizeGroup.add(sizeHumaine);
        sizeGroup.add(sizeOgresse);
        sizePanel.add(sizeNaine);
        sizePanel.add(sizeHumaine);
        sizePanel.add(sizeOgresse);

        sizeNaine.addActionListener(e -> updatePrices());
        sizeHumaine.addActionListener(e -> updatePrices());
        sizeOgresse.addActionListener(e -> updatePrices());

        panel.add(sizePanel);

        // Panel for pizza list with scroll
        pizzaListPanel = new JPanel();
        pizzaListPanel.setLayout(new BoxLayout(pizzaListPanel, BoxLayout.Y_AXIS));
        JScrollPane pizzaScrollPane = new JScrollPane(pizzaListPanel);
        pizzaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pizzaScrollPane.setPreferredSize(new Dimension(780, 400));

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nomPizza, prixBase FROM pizza");

            while (rs.next()) {
                String pizzaName = rs.getString("nomPizza");
                int price = rs.getInt("prixBase");
                pizzaPrices.put(pizzaName, price);

                Statement stmtIngredients = conn.createStatement();
                ResultSet rsIngredients = stmtIngredients.executeQuery("SELECT nom FROM pizza_ingredients WHERE nomPizza = '" + pizzaName + "'");
                ArrayList<String> ingredients = new ArrayList<>();
                while (rsIngredients.next()) {
                    ingredients.add(rsIngredients.getString("nom"));
                }
                pizzaIngredients.put(pizzaName, String.join(", ", ingredients));

                JPanel pizzaPanel = new JPanel(new BorderLayout());
                JRadioButton pizzaButton = new JRadioButton(pizzaName + " - " + price + "€");
                pizzaGroup.add(pizzaButton);
                pizzaPanel.add(pizzaButton, BorderLayout.NORTH);

                JTextArea ingredientArea = new JTextArea(pizzaIngredients.get(pizzaName));
                ingredientArea.setLineWrap(true);
                ingredientArea.setWrapStyleWord(true);
                ingredientArea.setEditable(false);
                pizzaPanel.add(ingredientArea, BorderLayout.CENTER);

                pizzaListPanel.add(pizzaPanel);
            }

            // Get client details
            rs = stmt.executeQuery("SELECT credit FROM client WHERE idClient = " + clientId);
            if (rs.next()) {
                solde = rs.getInt("credit");
            }

            // Check if the client has a free pizza available
            rs = stmt.executeQuery("SELECT COUNT(*) AS orderCount FROM commande WHERE idClient = " + clientId);
            if (rs.next()) {
                int orderCount = rs.getInt("orderCount");
                freePizzaAvailable = (orderCount % 10 == 0) && orderCount > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.add(pizzaScrollPane);

        priceLabel = new JLabel("Prix: ");
        panel.add(priceLabel);

        JButton orderButton = new JButton("Commander");
        JButton cancelButton = new JButton("Annuler la commande");

        orderButton.addActionListener(e -> placeOrder());

        cancelButton.addActionListener(e -> {
            new ClientInterface(clientId);
            frame.dispose();
        });

        panel.add(orderButton);
        panel.add(cancelButton);
        frame.add(new JScrollPane(panel));
        frame.setVisible(true);

        updatePrices(); // Initial price update
    }

    private void updatePrices() {
        for (Enumeration<AbstractButton> buttons = pizzaGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            String buttonText = button.getText();
            String pizzaName = buttonText.split(" - ")[0];
            int basePrice = pizzaPrices.get(pizzaName);

            int updatedPrice = basePrice;
            if (sizeGroup.getSelection() != null) {
                String selectedSize = sizeGroup.getSelection().getActionCommand();
                switch (selectedSize) {
                    case "Naine":
                        updatedPrice = (int) (basePrice * 0.66);
                        break;
                    case "Humaine":
                        updatedPrice = basePrice;
                        break;
                    case "Ogresse":
                        updatedPrice = (int) (basePrice * 1.33);
                        break;
                }
            }

            button.setText(pizzaName + " - " + updatedPrice + "€");
        }

        ButtonModel selectedModel = pizzaGroup.getSelection();
        if (selectedModel != null) {
            for (Enumeration<AbstractButton> buttons = pizzaGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.getModel() == selectedModel) {
                    String selectedPizza = button.getText().split(" - ")[0];
                    int selectedPrice = pizzaPrices.get(selectedPizza);
                    priceLabel.setText("Prix: " + selectedPrice + "€");
                    break;
                }
            }
        }
    }

    private void placeOrder() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Select a random delivery person
            ResultSet rs = stmt.executeQuery("SELECT matricule FROM livreur ORDER BY RAND() LIMIT 1");
            rs.next();
            int deliveryPersonId = rs.getInt("matricule");

            // Select a random vehicle
            rs = stmt.executeQuery("SELECT immatriculation FROM vehicule ORDER BY RAND() LIMIT 1");
            rs.next();
            String vehicleId = rs.getString("immatriculation");

            // Get selected pizza
            ButtonModel selectedPizzaModel = pizzaGroup.getSelection();
            if (selectedPizzaModel != null) {
                for (Enumeration<AbstractButton> buttons = pizzaGroup.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    if (button.getModel() == selectedPizzaModel) {
                        String selectedPizza = button.getText().split(" - ")[0];
                        String pizzaSize = sizeGroup.getSelection().getActionCommand();
                        int pizzaPrice = Integer.parseInt(button.getText().split(" - ")[1].replace("€", "").trim());
                        boolean isPizzaFree = false;

                        // Check if the client has enough credit or a free pizza
                        if (freePizzaAvailable) {
                            isPizzaFree = true;
                            JOptionPane.showMessageDialog(frame, "Vous avez droit à une pizza gratuite!", "Pizza gratuite", JOptionPane.INFORMATION_MESSAGE);
                        } else if (solde < pizzaPrice) {
                            JOptionPane.showMessageDialog(frame, "Solde insuffisant pour passer la commande.", "Solde insuffisant", JOptionPane.ERROR_MESSAGE);
                            return;
                        } else {
                            solde -= pizzaPrice;
                            PreparedStatement updateCreditStmt = conn.prepareStatement("UPDATE client SET credit = ? WHERE idClient = ?");
                            updateCreditStmt.setInt(1, solde);
                            updateCreditStmt.setInt(2, clientId);
                            updateCreditStmt.executeUpdate();
                        }

                        // Calculate random delivery time between 20 and 45 minutes from now
                        Random random = new Random();
                        int deliveryTimeMinutes = 20 + random.nextInt(26);
                        Timestamp deliveryTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(deliveryTimeMinutes));

                        // Insert the new order into the database
                        PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO commande (dateCommande, dateLivraison, taillePizza, immatriculation, matricule, nomPizza, idClient,  offerte) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                        pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                        pstmt.setTimestamp(2, deliveryTime);
                        pstmt.setString(3, pizzaSize);
                        pstmt.setString(4, vehicleId);
                        pstmt.setInt(5, deliveryPersonId);
                        pstmt.setString(6, selectedPizza);
                        pstmt.setInt(7, clientId);
                        pstmt.setBoolean(8, isPizzaFree);
                        pstmt.executeUpdate();

                        JOptionPane.showMessageDialog(frame, "Commande passée avec succès!");
                        new ClientInterface(clientId);
                        frame.dispose();
                        return;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Veuillez sélectionner une pizza.");
            }
        } catch (SQLException  e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erreur lors de la passation de la commande.");
        }
    }
}
