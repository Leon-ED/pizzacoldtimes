package fr.esiee;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.Duration;
import java.util.Vector;

class ClientPanel extends JPanel {
    private JTable clientTable;
    private JTextField searchField;
    private JButton searchButton, commandesButton;

    public ClientPanel() {
        setLayout(new BorderLayout());

        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> searchClients());

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Recherche Client:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        clientTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(clientTable);

        commandesButton = new JButton("Voir Commandes");
        commandesButton.addActionListener(e -> openCommandesPanel());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(commandesButton);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadClients();
    }

    private void loadClients() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM client");

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Prénom");
            columnNames.add("Nom");
            columnNames.add("Email");
            columnNames.add("Adresse");
            columnNames.add("Crédit");
            columnNames.add("Nombre de Commandes");
            columnNames.add("Argent Dépensé (€)");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                int clientId = rs.getInt("idClient");
                row.add(clientId);
                row.add(rs.getString("prenom"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("email"));
                row.add(rs.getString("adresse"));
                row.add(rs.getInt("credit"));

                int[] ordersAndSpent = getOrdersAndSpent(clientId);
                row.add(ordersAndSpent[0]);
                row.add(ordersAndSpent[1]);

                data.add(row);
            }

            clientTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int[] getOrdersAndSpent(int clientId) {
        int[] ordersAndSpent = new int[2]; // [0] = number of orders, [1] = total spent
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT taillePizza, nomPizza, offerte, dateCommande, dateLivraison FROM commande WHERE idClient = ?");
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            int totalOrders = 0;
            int totalSpent = 0;

            while (rs.next()) {
                String taillePizza = rs.getString("taillePizza");
                String nomPizza = rs.getString("nomPizza");
                boolean offerte = rs.getBoolean("offerte");
                Timestamp dateCommande = rs.getTimestamp("dateCommande");
                Timestamp dateLivraison = rs.getTimestamp("dateLivraison");

                if (!offerte && isOnTime(dateCommande, dateLivraison)) {
                    int prixBase = getPizzaPrice(nomPizza);
                    int prix = (int) (prixBase * (taillePizza.equals("Naine") ? 0.66 : taillePizza.equals("Ogresse") ? 1.33 : 1));
                    totalSpent += prix;
                }

                totalOrders++;
            }

            ordersAndSpent[0] = totalOrders;
            ordersAndSpent[1] = totalSpent;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersAndSpent;
    }

    private boolean isOnTime(Timestamp dateCommande, Timestamp dateLivraison) {
        long duration = Duration.between(dateCommande.toLocalDateTime(), dateLivraison.toLocalDateTime()).toMinutes();
        return duration <= 45;
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

    private void searchClients() {
        String keyword = searchField.getText();
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM client WHERE prenom LIKE ? OR nom LIKE ? OR email LIKE ? OR adresse LIKE ?");
            String searchQuery = "%" + keyword + "%";
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);
            pstmt.setString(3, searchQuery);
            pstmt.setString(4, searchQuery);
            ResultSet rs = pstmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Prénom");
            columnNames.add("Nom");
            columnNames.add("Email");
            columnNames.add("Adresse");
            columnNames.add("Crédit");
            columnNames.add("Nombre de Commandes");
            columnNames.add("Argent Dépensé (€)");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                int clientId = rs.getInt("idClient");
                row.add(clientId);
                row.add(rs.getString("prenom"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("email"));
                row.add(rs.getString("adresse"));
                row.add(rs.getInt("credit"));

                int[] ordersAndSpent = getOrdersAndSpent(clientId);
                row.add(ordersAndSpent[0]);
                row.add(ordersAndSpent[1]);

                data.add(row);
            }

            clientTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openCommandesPanel() {
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1) {
            int clientId = (int) clientTable.getValueAt(selectedRow, 0);
            new OrdersList(clientId);
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class CustomTableModel extends javax.swing.table.DefaultTableModel {
    CustomTableModel(Vector<Vector<Object>> data, Vector<String> columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // All cells are not editable
    }
}
