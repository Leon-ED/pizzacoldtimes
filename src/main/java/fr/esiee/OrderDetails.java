package fr.esiee;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.Duration;

public class OrderDetails extends JFrame {
    public static final int PIZZA_IS_LATE = 30;

    public OrderDetails(int orderId, int clientId) {
        setTitle("Détails de la Commande");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel orderDetails = new JLabel(getOrderDetails(orderId, clientId));
        orderDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton backButton = new JButton("Retour à la liste des commandes");
        backButton.addActionListener(e -> {
            new OrdersList(clientId);
            dispose();
        });

        JButton printButton = new JButton("Imprimer le reçu");
        printButton.addActionListener(e -> printReceipt(orderId, clientId));

        panel.add(orderDetails);
        panel.add(Box.createVerticalStrut(20)); // Add space between components
        panel.add(printButton);
        panel.add(backButton);

        add(panel);
        setVisible(true);
    }

    private String getOrderDetails(int orderId, int clientId) {
        StringBuilder details = new StringBuilder("<html>");
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM commande WHERE numero = ?");
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
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

                // Get client details
                String clientDetails = getClientDetails(clientId);

                // Format order details
                details.append(String.format(
                        "Commande N°%d<br>Date de commande: %s à %s<br>Pizza: %s<br>Ingrédients: %s<br>Taille: %s<br>Livré en %d minutes par %s en %s<br>%s",
                        orderId,
                        dateCommande.toLocalDateTime().toLocalDate(),
                        dateCommande.toLocalDateTime().toLocalTime(),
                        nomPizza,
                        ingredients,
                        taillePizza,
                        duration,
                        livreurDetails,
                        vehicleDetails,
                        clientDetails
                ));

                // If the pizza was late
                if (duration > PIZZA_IS_LATE) {
                    details.append("<br><span style='color:red;'>Pizza offerte due au retard</span>");
                    details.append(String.format("<br><span style='text-decoration:line-through;'>Prix: %d€</span>", prix));
                } else {
                    details.append(String.format("<br>Prix: %s€ %s",
                            offerte ? 0 : prix,
                            offerte ? "(programme fidélité)" : ""));
                }
            }
            details.append("</html>");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details.toString();
    }

    private void printReceipt(int orderId, int clientId) {
        try {
            String fileName = "recu_commande_" + orderId + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Add order details to the PDF
            String orderDetails = getOrderDetails(orderId, clientId).replaceAll("<br>", "\n").replaceAll("<[^>]*>", "");
            document.add(new Paragraph(orderDetails));

            document.close();
            JOptionPane.showMessageDialog(this, "Reçu imprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'impression du reçu.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getClientDetails(int clientId) {
        StringBuilder clientDetails = new StringBuilder();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT prenom, nom, adresse, email FROM client WHERE idClient = ?");
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                clientDetails.append("Client: ").append(rs.getString("prenom")).append(" ").append(rs.getString("nom"))
                        .append("<br>Livrée à: ").append(rs.getString("adresse"))
                        .append("<br>Contact: ").append(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientDetails.toString();
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
