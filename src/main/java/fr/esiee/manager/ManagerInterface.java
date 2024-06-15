package fr.esiee.manager;

import javax.swing.*;

public class ManagerInterface {
    public ManagerInterface() {
        JFrame frame = new JFrame("Interface Gestionnaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Clients", new ClientPanel());
        tabbedPane.addTab("Pizzas", new PizzaPanel());
        tabbedPane.addTab("Ingrédients", new IngredientPanel());
        tabbedPane.addTab("Statistiques", new StatisticsPanel());
        tabbedPane.addTab("Livreurs", new LivreurPanel());
        tabbedPane.addTab("Véhicules", new VehiculePanel());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }
}


