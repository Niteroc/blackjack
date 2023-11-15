package server;

import client.Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        tpe = new ThreadPoolExecutor(MAX_CLIENTS*5, MAX_CLIENTS*5, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

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

                if (true) {
                    try{
                        ClientHandler clientHandler = new ClientHandler(clientSocket, tableHandler);
                        tpe.execute(clientHandler);
                    }catch (Exception e){
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
}
