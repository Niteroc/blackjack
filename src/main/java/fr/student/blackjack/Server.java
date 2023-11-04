package fr.student.blackjack;

import java.io.IOException;
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
    static final List<ClientHandler> clients = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void afficherNombreClient() {
        logger.info("Connexion " + clients.size() + "/" + MAX_CLIENTS);
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(
                MAX_CLIENTS, MAX_CLIENTS,
                60L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );

        try {
            serverSocket = new ServerSocket(PORT);
            logger.info("Serveur en attente de connexions sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nouvelle connexion acceptée.");

                if (clients.size() < MAX_CLIENTS) {
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    tpe.execute(clientHandler);
                    afficherNombreClient();
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
}
