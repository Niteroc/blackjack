package server;

import client.Client;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    public Socket getClientSocket() {
        return clientSocket;
    }

    private final Socket clientSocket;


    public Client getClient() {
        return client;
    }

    private Client client;

    private final ObjectInputStream readerObject;
    private final TableHandler tableHandler;

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket clientSocket, TableHandler tableHandler) throws IOException, URISyntaxException {
        this.clientSocket = clientSocket;
        this.tableHandler = tableHandler;

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

    @Override
    public void run() {
        try {
            while (true) { // en écoute des maj du joueur
                client = (Client) readerObject.readObject();
                System.out.println("Mise à jour reçue du client (" + client.getId() + ") - " + client.getPseudo() + " de la table " + TableHandler.getId());
                tableHandler.updateClient(client);
            }

        } catch (Exception e) {
            logger.log(Level.INFO, "Fin de la communication avec le client " + client.getPseudo());
        } finally {
            try {
                clientSocket.close();
                tableHandler.removeClientHandler(this);
                logger.info("Client " + client.getPseudo() + " déconnecté.");
                saveClient(client);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erreur lors de la déconnexion du client ", e);
            }
        }
    }

    public static Client findInList(List<Client> clientList, Client clientToFind){
        for(Client client : clientList){
            if(client.getPseudo().equals(clientToFind.getPseudo()))return client;
        }
        return clientToFind;
    }

    public void saveClient(Client client) {
        List<Client> clients = loadClientsList();
        clients.remove(findInList(clients, client));
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

    @SuppressWarnings("unchecked") // pour le cast de object en List<Client>
    public static List<Client> loadClientsList() {
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream("clients.ser"))) {
            Object object = reader.readObject();
            if (object instanceof List) {
                List<Client> loadedClients = (List<Client>) object;
                logger.info("Liste de clients chargée depuis le fichier.");
                return loadedClients;
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
        return "ClientHandler{" +
                "client=" + client +
                '}';
    }
}
