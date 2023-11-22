package server;

import client.Client;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère les communications avec un client connecté au serveur de Blackjack.
 */
public class ClientHandler implements Runnable {

    /**
     * Retourne le socket du client.
     *
     * @return Le socket du client.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    private final Socket clientSocket;

    /**
     * Retourne le client associé à ce gestionnaire de client.
     *
     * @return Le client associé.
     */
    public Client getClient() {
        return client;
    }

    private Client client;

    private final ObjectInputStream readerObject;
    private final TableHandler tableHandler;

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private Server server;

    /**
     * Initialise le gestionnaire de client avec le socket du client, le gestionnaire de table et le serveur.
     *
     * @param clientSocket Le socket du client.
     * @param tableHandler Le gestionnaire de table associé.
     * @param server       Le serveur associé.
     * @throws IOException          En cas d'erreur d'entrée/sortie.
     * @throws InterruptedException En cas d'interruption de l'exécution.
     */
    public ClientHandler(Socket clientSocket, TableHandler tableHandler, Server server) throws IOException, InterruptedException {
        this.clientSocket = clientSocket;
        this.tableHandler = tableHandler;
        this.server = server;

        readerObject = new ObjectInputStream(clientSocket.getInputStream());

        try {
            client = (Client) readerObject.readObject();
        } catch (ClassNotFoundException exc) {
        }

        Server.clientLogin(client);
        Server.logPlayerCount();

        logger.info(client + " a rejoint la table " + TableHandler.getId());

        tableHandler.addClientHandler(this);
        tableHandler.updateClient(client);

    }

    /**
     * Démarre le traitement des mises à jour du client.
     */
    @Override
    public void run() {
        try {
            while (true) { // en écoute des maj du joueur
                client = (Client) readerObject.readObject();
                System.out.println("Mise à jour reçue du client  : " + client);
                tableHandler.updateClient(client);
            }

        } catch (Exception e) {
            logger.log(Level.INFO, "Fin de la communication avec le client " + client.getPseudo());
        } finally {
            try {
                clientSocket.close();
                tableHandler.removeClientHandler(this);
                logger.info("Client " + client.getPseudo() + " déconnecté.");
                server.saveClient(client);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erreur lors de la déconnexion du client ", e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client);
    }

    @Override
    public String toString() {
        return "ClientHandler{" + "client=" + client + '}';
    }
}
