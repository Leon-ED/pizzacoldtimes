package fr.esiee;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.Duration;

public class OrdersList extends JFrame {

    public OrdersList(int clientId) {
        String clientName = getClientName(clientId);
        setTitle("Liste des Commandes de " + clientName);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(ordersPanel);

        loadOrders(clientId, ordersPanel);

        add(scrollPane);
        setVisible(true);
    }

    private String getClientName(int clientId) {
        String clientName = "";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT nom, prenom FROM client WHERE idClient = ?");
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                clientName = rs.getString("prenom") + " " + rs.getString("nom");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientName;
    }

    private void loadOrders(int clientId, JPanel ordersPanel) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM commande WHERE idClient = ?");
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("numero");
                Timestamp dateCommande = rs.getTimestamp("dateCommande");
                Timestamp dateLivraison = rs.getTimestamp("dateLivraison");
                String nomPizza = rs.getString("nomPizza");
                String taillePizza = rs.getString("taillePizza");
                String immatriculation = rs.getString("immatriculation");
                int matriculeLivreur = rs.getInt("matricule");
                boolean offerte = rs.getBoolean("offerte");

                // Calculate delivery duration in minutes
                long duration = Duration.between(dateCommande.toLocalDateTime(), dateLivraison.toLocalDateTime()).toMinutes();

                // Get livreur and vehicle details
                String livreurDetails = getLivreurDetails(matriculeLivreur);
                String vehicleDetails = getVehicleDetails(immatriculation);
                String ingredients = getPizzaIngredients(nomPizza);

                // Calculate price based on pizza size
                int prixBase = getPizzaPrice(nomPizza);
                int prix = (int) (prixBase * (taillePizza.equals("Naine") ? 0.66 : taillePizza.equals("Ogresse") ? 1.33 : 1));

                // Format order details
                String orderDetails = String.format(
                        "<html>Commande N°%d le %s à %s<br>Pizza: %s<br>Ingrédients: %s<br>Taille: %s<br>Livré en %d minutes par %s en %s<br>Prix: %s€ %s%s</html>",
                        orderId,
                        dateCommande.toLocalDateTime().toLocalDate(),
                        dateCommande.toLocalDateTime().toLocalTime(),
                        nomPizza,
                        ingredients,
                        taillePizza,
                        duration,
                        livreurDetails,
                        vehicleDetails,
                        offerte ? 0 : prix,
                        offerte ? "(programme fidélité)" : "",
                        duration > 45 ? "<br><span style='color:red;'>Pizza offerte due au retard</span>" : ""
                );

                // Create a label to display the order details
                JLabel orderLabel = new JLabel(orderDetails);
                orderLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Reduced border
                orderLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                orderLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        new OrderDetails(orderId, clientId);
                        dispose();
                    }
                });
                ordersPanel.add(orderLabel);
                ordersPanel.add(new JSeparator());
            }

            // Add back to client interface button
            JButton backButton = new JButton("Retour à l'accueil client");
            backButton.addActionListener(e -> {
                new ClientInterface(clientId);
                dispose();
            });
            ordersPanel.add(backButton);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getLivreurDetails(int matriculeLivreur) {
        String livreurDetails = "";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT prenom, nom FROM livreur WHERE matricule = ?");
            pstmt.setInt(1, matriculeLivreur);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                livreurDetails = rs.getString("prenom") + " " + rs.getString("nom");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livreurDetails;
    }

    private String getVehicleDetails(String immatriculation) {
        String vehicleDetails = "";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT type, modele, marque FROM vehicule WHERE immatriculation = ?");
            pstmt.setString(1, immatriculation);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                vehicleDetails = String.format("%s %s %s (%s)", rs.getString("type"), rs.getString("marque"), rs.getString("modele"), immatriculation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicleDetails;
    }

    private int getPizzaPrice(String nomPizza) {
        int prix = 0;
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT prixBase FROM pizza WHERE nomPizza = ?");
            pstmt.setString(1, nomPizza);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                prix = rs.getInt("prixBase");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prix;
    }

    private String getPizzaIngredients(String nomPizza) {
        StringBuilder ingredients = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM pizza_ingredients WHERE nomPizza = ?");
            pstmt.setString(1, nomPizza);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (ingredients.length() > 0) {
                    ingredients.append(", ");
                }
                ingredients.append(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredients.toString();
    }
}
