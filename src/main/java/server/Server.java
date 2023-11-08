package server;

import table.TableSR;

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
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static ThreadPoolExecutor tpe;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        tpe = new ThreadPoolExecutor(MAX_CLIENTS, MAX_CLIENTS, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        try {
            serverSocket = new ServerSocket(PORT);
            logger.info("Serveur en attente de connexions sur le port " + PORT);


            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nouvelle connexion acceptée.");

                if (true) {
                    TableHandler tableHandler = new TableHandler();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, tableHandler);
                    tpe.execute(clientHandler);
                    tpe.execute(tableHandler);
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
