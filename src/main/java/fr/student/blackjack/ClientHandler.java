package fr.student.blackjack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final int clientId;

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.clientId = Server.clients.size();
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                logger.info("Connexion de " + message + " (id:" + clientId + ")");
                writer.println(0);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur de communication avec le client " + clientId, e);
        } finally {
            try {
                clientSocket.close();
                logger.info("main.java.Client " + clientId + " déconnecté.");
                Server.clients.remove(this);
                Server.afficherNombreClient();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erreur lors de la déconnexion du client " + clientId, e);
            }
        }
    }
}
