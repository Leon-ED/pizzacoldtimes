package fr.esiee.pizzacoldtimes.client;

import fr.esiee.pizzacoldtimes.database.DatabaseConnection;
import fr.esiee.pizzacoldtimes.shared.OrdersList;
import fr.esiee.pizzacoldtimes.Main;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ClientInterface {

    private final int clientId;
    private String clientName;
    private int solde;
    private int totalOrders;
    private int ordersUntilFreePizza;
    private JLabel clientDetails;

    public ClientInterface(int clientId) {
        this.clientId = clientId;

        // Fetch client data and order information from the database
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM client WHERE idClient = " + clientId);
            rs.next();
            clientName = rs.getString("nom") + " " + rs.getString("prenom");
            solde = rs.getInt("credit");

            // Fetch total orders for the client
            rs = stmt.executeQuery("SELECT COUNT(*) AS totalOrders FROM commande WHERE idClient = " + clientId);
            rs.next();
            totalOrders = rs.getInt("totalOrders");

            // Calculate orders until free pizza
            int ordersForFreePizza = 10;
            ordersUntilFreePizza = totalOrders % (ordersForFreePizza + 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Interface Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel(new BorderLayout());

        clientDetails = new JLabel("Client: " + clientName + " - Solde: " + solde + "€");
        JLabel orderDetails = new JLabel("Nombre de commandes: " + totalOrders + " - Compteur fidélité: " + ordersUntilFreePizza + "/10");
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(clientDetails);
        infoPanel.add(orderDetails);

        panel.add(infoPanel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4));
        JButton orderButton = new JButton("Faire une commande");
        JButton ordersListButton = new JButton("Liste des commandes");
        JButton addCreditButton = new JButton("Ajouter solde");
        JButton logoutButton = new JButton("Déconnexion");

        buttonsPanel.add(orderButton);
        buttonsPanel.add(ordersListButton);
        buttonsPanel.add(addCreditButton);
        buttonsPanel.add(logoutButton);

        panel.add(buttonsPanel, BorderLayout.CENTER);
        frame.add(panel);
        frame.setVisible(true);

        orderButton.addActionListener(e -> {
            new OrderMenu(clientId);
            frame.dispose();
        });

        ordersListButton.addActionListener(e -> {
            new OrdersList(clientId);
            frame.dispose();
        });

        addCreditButton.addActionListener(e -> addCredit());

        logoutButton.addActionListener(e -> {
            new Main();
            frame.dispose();
        });
    }

    private void addCredit() {
        String input = JOptionPane.showInputDialog("Entrez le montant à ajouter au solde:");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int creditToAdd = Integer.parseInt(input.trim());
                if (creditToAdd <= 0) {
                    JOptionPane.showMessageDialog(null, "Le montant doit être supérieur à zéro.", "Montant invalide", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                solde += creditToAdd;

                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("UPDATE client SET credit = ? WHERE idClient = ?");
                pstmt.setInt(1, solde);
                pstmt.setInt(2, clientId);
                pstmt.executeUpdate();

                clientDetails.setText("Client: " + clientName + " - Solde: " + solde + "€");
                JOptionPane.showMessageDialog(null, "Votre solde a été augmenté de " + creditToAdd + "€.", "Crédit ajouté", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Veuillez entrer un montant valide.", "Montant invalide", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour du solde.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
