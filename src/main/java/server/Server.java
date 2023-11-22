package server;

import client.Client;
import table.HandSR;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe représentant le serveur du jeu de Blackjack.
 */
public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 5;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static final List<Client> listClientConnected = new ArrayList<>();

    /**
     * Méthode pour gérer la connexion d'un client.
     * Ajoute le client à la liste des clients connectés.
     *
     * @param client Le client connecté à ajouter à la liste.
     */
    public synchronized static void clientLogin(Client client) {
        listClientConnected.add(client);
    }

    /**
     * Méthode pour gérer la déconnexion d'un client.
     * Supprime le client de la liste des clients connectés.
     *
     * @param client Le client déconnecté à supprimer de la liste.
     */
    public synchronized static void clientLogout(Client client) {
        listClientConnected.remove(client);
    }

    /**
     * Méthode pour charger la liste des clients à partir d'un fichier sérialisé.
     *
     * @return La liste des clients chargée depuis le fichier ou une liste vide si le fichier est vide ou n'existe pas.
     */
    @SuppressWarnings("unchecked")
    public static List<Client> loadClientsList() {
        File clientsFile = new File("clients.ser");
        if (!clientsFile.exists() || clientsFile.length() == 0) {
            logger.warning("Le fichier pour les clients est vide ou n'existe pas.");
            return new ArrayList<>();
        }

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(clientsFile))) {
            Object object = reader.readObject();
            if (object instanceof List) {
                return (List<Client>) object;
            } else {
                logger.warning("Le fichier ne contient pas une liste de clients valide.");
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Fichier pour les clients introuvable.", e);
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Erreur lors du chargement de la liste des clients depuis le fichier.", e);
        }
        return new ArrayList<>();
    }

    /**
     * Méthode principale pour lancer le serveur.
     * Crée une instance du serveur.
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        new Server();
    }

    /**
     * Constructeur de la classe Server.
     * Initialise le serveur et gère les connexions des clients.
     */
    public Server() {

        ServerSocket serverSocket = null;
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(MAX_CLIENTS * 5, MAX_CLIENTS * 5, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        try {
            serverSocket = new ServerSocket(PORT);
            logger.info("Serveur en attente de connexions sur le port " + PORT);
            TableHandler tableHandler = new TableHandler();
            logger.info("La table " + TableHandler.getId() + " a été créée");
            tpe.execute(tableHandler);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nouvelle connexion acceptée.");

                ObjectOutputStream writerObject = new ObjectOutputStream(clientSocket.getOutputStream());
                writerObject.writeObject(listClientConnected);

                writerObject = new ObjectOutputStream(clientSocket.getOutputStream());
                writerObject.writeObject(loadClientsList());

                if (true) {
                    try {
                        ClientHandler clientHandler = new ClientHandler(clientSocket, tableHandler, this);
                        tpe.execute(clientHandler);
                    } catch (Exception e) {
                        logger.info("Fermeture de la connexion");
                    }
                } else {
                    logger.warning("Nombre maximum de clients atteint. Nouvelle connexion refusée.");
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'exécution du serveur", e);
        } finally {
            tpe.shutdown();
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Erreur lors de la fermeture du serveur", e);
                }
            }
        }
    }

    public static void logPlayerCount() {
        logger.info("Clients connectés au serveur : " + listClientConnected.toString());
    }

    /**
     * Méthode pour enregistrer un client dans un fichier sérialisé.
     * Réinitialise les informations du client avant l'enregistrement.
     *
     * @param client Le client à enregistrer dans le fichier.
     * @throws IOException En cas d'erreur d'entrée/sortie lors de l'enregistrement.
     */
    public void saveClient(Client client) throws IOException {

        List<Client> clients = loadClientsList();

        try {
            clients.remove(findInList(clients, client));
        } catch (Exception e) {
            // ne rien faire -- fichier vide
        }

        client.setCurrentHand(new HandSR());
        client.setHasBet(false, 0, false);
        client.setMyTurn(false);
        client.setEndTurn(false);
        client.setWantACard(false, false);
        client.setWantToStay(false, false);
        client.setGain(0);
        clients.add(client);
        Server.clientLogout(client);
        Server.logPlayerCount();

        try (ObjectOutputStream writer = new ObjectOutputStream(Files.newOutputStream(Paths.get("clients.ser")))) {
            writer.writeObject(clients);
            writer.flush();
            logger.info("Client " + client.getPseudo() + " enregistré dans le fichier sérialisé.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'enregistrement du client dans le fichier sérialisé.", e);
        }
    }

    /**
     * Méthode pour rechercher un client dans une liste de clients.
     *
     * @param clientList   La liste de clients dans laquelle effectuer la recherche.
     * @param clientToFind Le client à rechercher dans la liste.
     * @return Le client trouvé dans la liste ou le client à trouver s'il n'est pas présent.
     */
    public static Client findInList(List<Client> clientList, Client clientToFind) throws IOException {
        for (Client client : clientList) {
            if (client.getPseudo().equals(clientToFind.getPseudo())) return client;
        }
        clientToFind.setBalance(10000, false);
        return clientToFind;
    }
}
