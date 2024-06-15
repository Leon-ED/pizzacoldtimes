package fr.esiee.pizzacoldtimes.client;

import fr.esiee.pizzacoldtimes.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ClientLoginInterface {
    private Map<String, Integer> clientMap;

    public ClientLoginInterface() {
        JFrame frame = new JFrame("Se connecter en tant que client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        clientMap = getClients();

        JPanel panel = new JPanel(new GridLayout(2, 1));
        JComboBox<String> clientDropdown = new JComboBox<>(clientMap.keySet().toArray(new String[0]));
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String selectedClient = (String) clientDropdown.getSelectedItem();
            int clientId = clientMap.get(selectedClient);
            new ClientInterface(clientId);
            frame.dispose();
        });

        panel.add(clientDropdown);
        panel.add(loginButton);
        frame.add(panel);
        frame.setVisible(true);
    }

    private Map<String, Integer> getClients() {
        Map<String, Integer> clients = new HashMap<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idClient, nom, prenom FROM client");
            while (rs.next()) {
                String fullName = rs.getString("prenom") + " " + rs.getString("nom");
                int idClient = rs.getInt("idClient");
                clients.put(fullName, idClient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clients;
    }
}
