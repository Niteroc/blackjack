# 🎲 Projet Blackjack

## Description
Ce projet est une implémentation du jeu de Blackjack avec une interface graphique en Java (JavaFX). Il inclut un client, un serveur, une gestion de table. Le projet permet de jouer en multijoueur via un serveur qui gère les connexions des clients et les tables de jeu.

## Fonctionnalités
- Interface graphique en JavaFX.
- Mode client-serveur pour les parties multijoueurs.
- Génération de QR codes pour se connecter.
- Gestion complète des cartes et des mains (tirage, calcul de score, etc.).

## Structure du Répertoire

```bash
📂 blackjack/
├── README.md                # Fichier de documentation
├── demo.png                 # Capture d'écran de démonstration du jeu
├── pom.xml                  # Fichier Maven pour la gestion des dépendances
└── src/
    └── main/
        ├── java/
        │   ├── client/
        │   │   └── Client.java              # Client de connexion au serveur de Blackjack
        │   ├── gui/
        │   │   ├── Controller.java          # Contrôleur pour l'interface graphique JavaFX
        │   │   └── JavaFXGUI.java           # Classe principale pour l'interface graphique JavaFX
        │   ├── module-info.java             # Module Java (Java 9+)
        │   ├── qrcode/
        │   │   ├── MyQr.java                # Générateur de QR code personnalisé
        │   │   └── QrCode.java              # Classe de gestion des QR codes
        │   ├── server/
        │   │   ├── ClientHandler.java       # Gestion des clients sur le serveur
        │   │   ├── Server.java              # Serveur pour gérer les parties de Blackjack
        │   │   └── TableHandler.java        # Gestion des tables de jeu
        │   └── table/
        │       ├── CardSR.java              # Classe représentant une carte
        │       ├── HandSR.java              # Classe représentant une main de joueur
        │       └── TableSR.java             # Classe représentant la table de jeu
        └── resources/
            ├── all/
            │   ├── [Images de cartes]       # Cartes du jeu (plusieurs fichiers PNG)
            ├── back_cards.png               # Image pour le dos des cartes
            ├── blackjack-icon.jpg           # Icône pour l'application
            └── fxml/
                ├── cards.fxml               # Interface graphique pour les cartes
                └── hello-view.fxml          # Interface principale de l'application
