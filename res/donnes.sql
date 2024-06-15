-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:3306
-- Généré le : sam. 15 juin 2024 à 13:26
-- Version du serveur : 8.0.30
-- Version de PHP : 8.2.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `pisseza`
--

--
-- Déchargement des données de la table `client`
--

INSERT INTO `client` (`idClient`, `prenom`, `email`, `adresse`, `credit`, `nom`) VALUES
(2, 'Jonathan', 'jb@mail.fr', 'Bercyn Paris 12', 167, 'Blois'),
(3, 'XD', 'xddl@mail.fr', 'Nantes', 170, 'DL'),
(4, 'Charlotte', 'c@mail.fr', 'Viroflay', 200, 'du Ski'),
(5, 'Rebecca', 'r@mail.fr', 'Inconnu', 10, 'du Ski');

--
-- Déchargement des données de la table `commande`
--

INSERT INTO `commande` (`numero`, `dateCommande`, `dateLivraison`, `taillePizza`, `immatriculation`, `matricule`, `nomPizza`, `idClient`, `offerte`) VALUES
(65, '2024-06-14 17:33:07', '2024-06-14 17:40:07', 'Humaine', 'CT-233-NZ', 8, 'La Spéciale', 2, 0),
(66, '2024-06-14 17:33:09', '2024-06-14 18:33:09', 'Humaine', 'CT-233-NZ', 8, 'Abordable', 2, 0),
(76, '2024-06-15 13:38:21', '2024-06-15 14:15:21', 'Humaine', 'CT-233-NZ', 8, 'Végan', 5, 0),
(77, '2024-06-15 13:51:57', '2024-06-15 14:18:57', 'Ogresse', 'BA-801-TI', 8, '5 Fromages', 2, 0),
(78, '2024-06-15 13:52:34', '2024-06-15 14:23:34', 'Humaine', 'CT-233-NZ', 8, 'Abordable', 2, 0),
(79, '2024-06-15 13:52:37', '2024-06-15 14:32:37', 'Humaine', 'CT-233-NZ', 8, 'Végan', 2, 0),
(80, '2024-06-15 13:54:57', '2024-06-15 14:37:57', 'Humaine', 'CT-233-NZ', 8, '5 Fromages', 3, 0),
(81, '2024-06-15 13:54:59', '2024-06-15 14:19:59', 'Humaine', 'CT-233-NZ', 8, 'Végan', 3, 0),
(82, '2024-06-15 14:26:24', '2024-06-15 15:08:24', 'Ogresse', 'CT-233-NZ', 8, 'La Spéciale', 2, 0),
(83, '2024-06-15 14:26:56', '2024-06-15 15:10:56', 'Humaine', 'CT-233-NZ', 8, 'Végan', 2, 0),
(84, '2024-06-15 14:26:59', '2024-06-15 14:57:59', 'Humaine', 'BA-801-TI', 8, 'Océanique', 2, 0),
(85, '2024-06-15 14:27:02', '2024-06-15 15:05:02', 'Humaine', 'BA-801-TI', 8, 'Tunisienne', 2, 0),
(86, '2024-06-15 14:27:05', '2024-06-15 14:48:05', 'Humaine', 'BA-801-TI', 8, 'Tunisienne', 2, 0),
(87, '2024-06-15 14:27:17', '2024-06-15 14:50:17', 'Humaine', 'BA-801-TI', 8, 'Tunisienne', 2, 1),
(88, '2024-06-15 14:31:44', '2024-06-15 15:01:44', 'Humaine', 'CT-233-NZ', 8, 'Océanique', 2, 0),
(89, '2024-06-15 14:31:55', '2024-06-15 15:04:55', 'Humaine', 'BA-801-TI', 8, 'Océanique', 2, 0),
(90, '2024-06-15 14:31:58', '2024-06-15 14:52:58', 'Humaine', 'BA-801-TI', 8, 'Chêvre Miel', 2, 0),
(91, '2024-06-15 14:32:00', '2024-06-15 15:02:00', 'Humaine', 'BA-801-TI', 8, 'Tunisienne', 2, 0),
(92, '2024-06-15 14:32:03', '2024-06-15 15:00:03', 'Humaine', 'CT-233-NZ', 8, 'Chêvre Miel', 2, 0),
(93, '2024-06-15 14:32:06', '2024-06-15 15:10:06', 'Humaine', 'BA-801-TI', 8, 'La Spéciale', 2, 0),
(94, '2024-06-15 14:32:08', '2024-06-15 14:58:08', 'Humaine', 'BA-801-TI', 8, 'La Spéciale', 2, 0),
(95, '2024-06-15 14:32:10', '2024-06-15 14:58:10', 'Humaine', 'BA-801-TI', 8, 'La Spéciale', 2, 0),
(96, '2024-06-15 14:32:13', '2024-06-15 15:05:13', 'Humaine', 'BA-801-TI', 8, 'Tunisienne', 2, 0),
(97, '2024-06-15 14:32:17', '2024-06-15 15:14:17', 'Humaine', 'BA-801-TI', 8, '5 Fromages', 2, 1);

--
-- Déchargement des données de la table `ingredient`
--

INSERT INTO `ingredient` (`nom`) VALUES
('Bacon (Halal)'),
('Base Crême Fraiche'),
('Base Sauce Tomate'),
('Champignon (de Paris)'),
('Champignon Hallucinogènes'),
('Compté'),
('Fromage Chèvre'),
('Fromage KIRI'),
('Gorgonzolla'),
('Harang'),
('Harissa'),
('Jambon'),
('Mélange de viandes (Surmulot, Colombin et Raven)'),
('Merguez'),
('Miel'),
('Mozzarella'),
('Oignon'),
('Olives'),
('Parmesan'),
('Pate Cheesy'),
('Pepperoni'),
('Poivron'),
('Poulet'),
('Salade'),
('Sauce du chef'),
('Saumon'),
('Thon'),
('Tomme de Brie'),
('Tomme de Meaux'),
('Viande Hâchée');

--
-- Déchargement des données de la table `livreur`
--

INSERT INTO `livreur` (`matricule`, `prenom`, `nom`) VALUES
(8, 'Andi', 'Mali'),
(9, 'Ama', 'Zon');

--
-- Déchargement des données de la table `pizza`
--

INSERT INTO `pizza` (`nomPizza`, `prixBase`) VALUES
('5 Fromages', 10),
('Abordable', 5),
('Chêvre Miel', 13),
('La Spéciale', 30),
('Océanique', 17),
('Tunisienne', 10),
('Végan', 20);

--
-- Déchargement des données de la table `pizza_ingredients`
--

INSERT INTO `pizza_ingredients` (`nomPizza`, `nom`) VALUES
('5 Fromages', 'Base Crême Fraiche'),
('Chêvre Miel', 'Base Crême Fraiche'),
('Abordable', 'Base Sauce Tomate'),
('La Spéciale', 'Base Sauce Tomate'),
('Océanique', 'Base Sauce Tomate'),
('Tunisienne', 'Base Sauce Tomate'),
('Végan', 'Base Sauce Tomate'),
('5 Fromages', 'Compté'),
('5 Fromages', 'Fromage Chèvre'),
('Chêvre Miel', 'Fromage Chèvre'),
('5 Fromages', 'Fromage KIRI'),
('5 Fromages', 'Gorgonzolla'),
('Océanique', 'Harang'),
('Tunisienne', 'Harissa'),
('Chêvre Miel', 'Jambon'),
('Abordable', 'Mélange de viandes (Surmulot, Colombin et Raven)'),
('Tunisienne', 'Merguez'),
('Chêvre Miel', 'Miel'),
('5 Fromages', 'Mozzarella'),
('Végan', 'Oignon'),
('Tunisienne', 'Olives'),
('Végan', 'Olives'),
('Végan', 'Poivron'),
('Abordable', 'Sauce du chef'),
('La Spéciale', 'Sauce du chef'),
('Végan', 'Sauce du chef'),
('Océanique', 'Saumon'),
('Océanique', 'Thon');

--
-- Déchargement des données de la table `vehicule`
--

INSERT INTO `vehicule` (`immatriculation`, `type`, `modele`, `marque`) VALUES
('BA-801-TI', 'MOTO', '801 RR', 'Bati'),
('CT-233-NZ', 'VOITURE', 'DUSTER', 'DACIA'),
('PT-808-DR', 'VOITURE', 'ALTEA', 'SEAT');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
