CREATE TABLE pizza(
   nomPizza VARCHAR(50) ,
   prixBase INT NOT NULL,
   PRIMARY KEY(nomPizza)
);

CREATE TABLE ingredient(
   nom VARCHAR(50) ,
   PRIMARY KEY(nom)
);

CREATE TABLE client(
   idClient INT AUTO_INCREMENT,
   prenom VARCHAR(50)  NOT NULL,
   email VARCHAR(50)  NOT NULL,
   adresse VARCHAR(255)  NOT NULL,
   credit INT,
   nom VARCHAR(50)  NOT NULL,
   PRIMARY KEY(idClient),
   UNIQUE(email)
);

CREATE TABLE vehicule(
   immatriculation VARCHAR(9) ,
   type VARCHAR(50)  NOT NULL,
   modele VARCHAR(25)  NOT NULL,
   marque VARCHAR(20)  NOT NULL,
   PRIMARY KEY(immatriculation)
);

CREATE TABLE livreur(
   matricule INT AUTO_INCREMENT,
   prenom VARCHAR(50)  NOT NULL,
   nom VARCHAR(50)  NOT NULL,
   PRIMARY KEY(matricule)
);

CREATE TABLE commande(
   numero INT AUTO_INCREMENT,
   dateCommande DATETIME NOT NULL,
   dateLivraison DATETIME,
   taillePizza VARCHAR(50) ,
   offerte BOOLEAN,
   immatriculation VARCHAR(9)  NOT NULL,
   matricule INT NOT NULL,
   nomPizza VARCHAR(50)  NOT NULL,
   idClient INT NOT NULL,
   PRIMARY KEY(numero),
   FOREIGN KEY(immatriculation) REFERENCES vehicule(immatriculation),
   FOREIGN KEY(matricule) REFERENCES livreur(matricule),
   FOREIGN KEY(nomPizza) REFERENCES pizza(nomPizza),
   FOREIGN KEY(idClient) REFERENCES client(idClient)
);

CREATE TABLE pizza_ingredients(
   nomPizza VARCHAR(50) ,
   nom VARCHAR(50) ,
   PRIMARY KEY(nomPizza, nom),
   FOREIGN KEY(nomPizza) REFERENCES pizza(nomPizza),
   FOREIGN KEY(nom) REFERENCES ingredient(nom)
);
