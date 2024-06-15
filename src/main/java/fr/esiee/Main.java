package fr.esiee;

import fr.esiee.client.ClientLoginInterface;
import fr.esiee.manager.ManagerInterface;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        JFrame frame = new JFrame("Pizza Cold Times");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(2, 1));
        JButton managerButton = new JButton("Se connecter en tant que gestionnaire");
        JButton clientButton = new JButton("Se connecter en tant que client");

        managerButton.addActionListener(e -> {
            new ManagerInterface();
            frame.dispose();
        });

        clientButton.addActionListener(e -> {
            new ClientLoginInterface();
            frame.dispose();
        });

        panel.add(managerButton);
        panel.add(clientButton);
        frame.add(panel);
        frame.setVisible(true);
    }
}
