package server;

import client.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 5;
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static ThreadPoolExecutor tpe;

    private static List<Client> listClientConnected = new ArrayList<>();

    public synchronized static void clientLogin(Client client){
        listClientConnected.add(client);
    }

    public synchronized static void clientLogout(Client client){
        listClientConnected.remove(client);
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

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        ServerSocket serverSocket = null;

        tpe = new ThreadPoolExecutor(MAX_CLIENTS * 5, MAX_CLIENTS * 5, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

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
                        saveClient(clientHandler.getClient());
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

    public static void logPlayerCount(){
        logger.info("Clients connectés au serveur : " + listClientConnected.toString());
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

    public static Client findInList(List<Client> clientList, Client clientToFind){
        for(Client client : clientList){
            if(client.getPseudo().equals(clientToFind.getPseudo()))return client;
        }
        return clientToFind;
    }
}
