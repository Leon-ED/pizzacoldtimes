package fr.esiee;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class StatisticsPanel extends JPanel {
    private JTextArea statsArea;

    public StatisticsPanel() {
        setLayout(new BorderLayout());

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statsArea);

        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.addActionListener(e -> loadStatistics());

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        loadStatistics();
    }

    private void loadStatistics() {
        StringBuilder stats = new StringBuilder();

        stats.append(getUnusedVehicles());
        stats.append(getOrderCounts());
        stats.append(getAverageOrders());
        stats.append(getClientsAboveAverage());
        stats.append(getBestClient());
        stats.append(getWorstDeliverer());
        stats.append(getMostLeastPopularPizza());
        stats.append(getFavoriteIngredient());

        statsArea.setText(stats.toString());
    }

    private String getUnusedVehicles() {
        StringBuilder result = new StringBuilder("Véhicules n'ayant jamais servi:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM vehicule WHERE immatriculation NOT IN (SELECT immatriculation FROM commande)");
            while (rs.next()) {
                result.append(rs.getString("immatriculation")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getOrderCounts() {
        StringBuilder result = new StringBuilder("Nombre de commandes par client:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, COUNT(*) AS orderCount FROM commande GROUP BY idClient");
            while (rs.next()) {
                result.append("Client ID ").append(rs.getInt("idClient")).append(": ").append(rs.getInt("orderCount")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getAverageOrders() {
        StringBuilder result = new StringBuilder("Moyenne des commandes:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT AVG(orderCount) AS averageOrders FROM (SELECT COUNT(*) AS orderCount FROM commande GROUP BY idClient) AS subquery");
            if (rs.next()) {
                result.append("Moyenne des commandes par client: ").append(rs.getDouble("averageOrders")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getClientsAboveAverage() {
        StringBuilder result = new StringBuilder("Clients ayant commandé plus que la moyenne:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, COUNT(*) AS orderCount FROM commande GROUP BY idClient HAVING orderCount > (SELECT AVG(orderCount) FROM (SELECT COUNT(*) AS orderCount FROM commande GROUP BY idClient) AS subquery)");
            while (rs.next()) {
                result.append("Client ID ").append(rs.getInt("idClient")).append(": ").append(rs.getInt("orderCount")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getBestClient() {
        StringBuilder result = new StringBuilder("Meilleur client:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, COUNT(*) AS orderCount FROM commande GROUP BY idClient ORDER BY orderCount DESC LIMIT 1");
            if (rs.next()) {
                result.append("Client ID ").append(rs.getInt("idClient")).append(": ").append(rs.getInt("orderCount")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getWorstDeliverer() {
        StringBuilder result = new StringBuilder("Pire livreur:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT matricule, COUNT(*) AS lateDeliveries FROM commande WHERE dateLivraison > dateCommande + INTERVAL 30 MINUTE GROUP BY matricule ORDER BY lateDeliveries DESC LIMIT 1");
            if (rs.next()) {
                result.append("Livreur ID ").append(rs.getInt("matricule")).append(": ").append(rs.getInt("lateDeliveries")).append(" livraisons en retard\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getMostLeastPopularPizza() {
        StringBuilder result = new StringBuilder("Pizzas les plus et les moins demandées:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nomPizza, COUNT(*) AS orderCount FROM commande GROUP BY nomPizza ORDER BY orderCount DESC LIMIT 1");
            if (rs.next()) {
                result.append("Pizza la plus demandée: ").append(rs.getString("nomPizza")).append(" avec ").append(rs.getInt("orderCount")).append(" commandes\n");
            }
            rs = stmt.executeQuery("SELECT nomPizza, COUNT(*) AS orderCount FROM commande GROUP BY nomPizza ORDER BY orderCount ASC LIMIT 1");
            if (rs.next()) {
                result.append("Pizza la moins demandée: ").append(rs.getString("nomPizza")).append(" avec ").append(rs.getInt("orderCount")).append(" commandes\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getFavoriteIngredient() {
        StringBuilder result = new StringBuilder("Ingrédient favori:\n");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nom, COUNT(*) AS ingredientCount FROM pizza_ingredients GROUP BY nom ORDER BY ingredientCount DESC LIMIT 1");
            if (rs.next()) {
                result.append("Ingrédient le plus utilisé: ").append(rs.getString("nom")).append(" avec ").append(rs.getInt("ingredientCount")).append(" utilisations\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
