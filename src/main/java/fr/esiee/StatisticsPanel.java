package fr.esiee;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

class StatisticsPanel extends JPanel {
    private JTextPane statsArea;

    public StatisticsPanel() {
        setLayout(new BorderLayout());

        statsArea = new JTextPane();
        statsArea.setContentType("text/html");
        statsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statsArea);

        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.addActionListener(e -> loadStatistics());

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        loadStatistics();
    }

    private void loadStatistics() {
        StringBuilder stats = new StringBuilder("<html>");

        stats.append("<b>Véhicules n'ayant jamais servi:</b><br>");
        stats.append(getUnusedVehicles());

        stats.append("<b>Nombre de commandes par client:</b><br>");
        stats.append(getOrderCounts());

        stats.append("<b>Moyenne des commandes:</b><br>");
        stats.append(getAverageOrders());

        stats.append("<b>Clients ayant commandé plus que la moyenne:</b><br>");
        stats.append(getClientsAboveAverage());

        stats.append("<b>Meilleur client:</b><br>");
        stats.append(getBestClient());

        stats.append("<b>Pire livreur:</b><br>");
        stats.append(getWorstDeliverer());

        stats.append("<b>Pizzas les plus et les moins demandées:</b><br>");
        stats.append(getMostLeastPopularPizza());

        stats.append("<b>Ingrédient favori:</b><br>");
        stats.append(getFavoriteIngredient());

        stats.append("<b>Jours les plus prolifiques:</b><br>");
        stats.append(getBusyDays());

        stats.append("<b>Montant total dépensé par chaque client:</b><br>");
        stats.append(getTotalSpentPerClient());

        stats.append("<b>Clients avec des commandes en retard:</b><br>");
        stats.append(getClientsWithLateOrders());

        stats.append("<b>Nombre total de pizzas commandées:</b><br>");
        stats.append(getTotalPizzasOrdered());

        stats.append("</html>");

        statsArea.setText(stats.toString());
    }

    private String getUnusedVehicles() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM vehicule WHERE immatriculation NOT IN (SELECT immatriculation FROM commande)");
            while (rs.next()) {
                result.append(rs.getString("type")).append(" ")
                        .append(rs.getString("marque")).append(" ")
                        .append(rs.getString("modele")).append(" (")
                        .append(rs.getString("immatriculation")).append(")<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getOrderCounts() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, COUNT(*) AS orderCount FROM commande GROUP BY idClient");
            while (rs.next()) {
                int clientId = rs.getInt("idClient");
                String clientDetails = getClientDetails(clientId);
                result.append(clientDetails).append(": ").append(rs.getInt("orderCount")).append("<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getAverageOrders() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT AVG(orderCount) AS averageOrders FROM (SELECT COUNT(*) AS orderCount FROM commande GROUP BY idClient) AS subquery");
            if (rs.next()) {
                result.append("Moyenne des commandes par client: ").append(rs.getDouble("averageOrders")).append("<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getClientsAboveAverage() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, COUNT(*) AS orderCount FROM commande GROUP BY idClient HAVING orderCount > (SELECT AVG(orderCount) FROM (SELECT COUNT(*) AS orderCount FROM commande GROUP BY idClient) AS subquery)");
            while (rs.next()) {
                int clientId = rs.getInt("idClient");
                String clientDetails = getClientDetails(clientId);
                result.append(clientDetails).append(": ").append(rs.getInt("orderCount")).append("<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getBestClient() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, COUNT(*) AS orderCount FROM commande GROUP BY idClient ORDER BY orderCount DESC LIMIT 1");
            if (rs.next()) {
                int clientId = rs.getInt("idClient");
                String clientDetails = getClientDetails(clientId);
                result.append(clientDetails).append(": ").append(rs.getInt("orderCount")).append("<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getWorstDeliverer() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT matricule, COUNT(*) AS lateDeliveries FROM commande WHERE dateLivraison > dateCommande + INTERVAL 45 MINUTE GROUP BY matricule ORDER BY lateDeliveries DESC LIMIT 1");
            if (rs.next()) {
                int delivererId = rs.getInt("matricule");
                String delivererDetails = getDelivererDetails(delivererId);
                result.append(delivererDetails).append(": ").append(rs.getInt("lateDeliveries")).append(" livraisons en retard<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getMostLeastPopularPizza() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nomPizza, COUNT(*) AS orderCount FROM commande GROUP BY nomPizza ORDER BY orderCount DESC LIMIT 1");
            if (rs.next()) {
                result.append("Pizza la plus demandée: ").append(rs.getString("nomPizza")).append(" avec ").append(rs.getInt("orderCount")).append(" commandes<br>");
            }
            rs = stmt.executeQuery("SELECT nomPizza, COUNT(*) AS orderCount FROM commande GROUP BY nomPizza ORDER BY orderCount ASC LIMIT 1");
            if (rs.next()) {
                result.append("Pizza la moins demandée: ").append(rs.getString("nomPizza")).append(" avec ").append(rs.getInt("orderCount")).append(" commandes<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getFavoriteIngredient() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT nom, COUNT(*) AS ingredientCount FROM pizza_ingredients GROUP BY nom ORDER BY ingredientCount DESC LIMIT 1");
            if (rs.next()) {
                result.append("Ingrédient le plus utilisé: ").append(rs.getString("nom")).append(" avec ").append(rs.getInt("ingredientCount")).append(" utilisations<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getBusyDays() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DAYOFWEEK(dateCommande) AS day, COUNT(*) AS orderCount FROM commande GROUP BY DAYOFWEEK(dateCommande) ORDER BY orderCount DESC");
            while (rs.next()) {
                DayOfWeek dayOfWeek = DayOfWeek.of(rs.getInt("day"));
                result.append(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.FRENCH)).append(": ").append(rs.getInt("orderCount")).append(" commandes<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getTotalSpentPerClient() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, SUM(CASE WHEN taillePizza = 'Naine' THEN prixBase * 0.66 WHEN taillePizza = 'Ogresse' THEN prixBase * 1.33 ELSE prixBase END) AS totalSpent FROM commande JOIN pizza ON commande.nomPizza = pizza.nomPizza WHERE commande.offerte = 0 GROUP BY idClient");
            while (rs.next()) {
                int clientId = rs.getInt("idClient");
                String clientDetails = getClientDetails(clientId);
                result.append(clientDetails).append(": ").append(rs.getDouble("totalSpent")).append("€<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getClientsWithLateOrders() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, COUNT(*) AS lateOrders FROM commande WHERE dateLivraison > dateCommande + INTERVAL 45 MINUTE GROUP BY idClient");
            while (rs.next()) {
                int clientId = rs.getInt("idClient");
                String clientDetails = getClientDetails(clientId);
                result.append(clientDetails).append(": ").append(rs.getInt("lateOrders")).append(" commandes en retard<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getTotalPizzasOrdered() {
        StringBuilder result = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS totalPizzas FROM commande");
            if (rs.next()) {
                result.append("Total de pizzas commandées: ").append(rs.getInt("totalPizzas")).append("<br>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getClientDetails(int clientId) {
        StringBuilder clientDetails = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT prenom, nom, email, adresse FROM client WHERE idClient = ?");
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                clientDetails.append(rs.getString("prenom")).append(" ").append(rs.getString("nom"))
                        .append(" (ID: ").append(clientId).append(")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientDetails.toString();
    }

    private String getDelivererDetails(int delivererId) {
        StringBuilder delivererDetails = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT prenom, nom FROM livreur WHERE matricule = ?");
            pstmt.setInt(1, delivererId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                delivererDetails.append(rs.getString("prenom")).append(" ").append(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delivererDetails.toString();
    }
}
