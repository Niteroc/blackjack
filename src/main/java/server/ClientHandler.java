package server;

import client.Client;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private int id;

    private static int number = 1;

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

    public ClientHandler(Socket clientSocket, TableHandler tableHandler) throws IOException, InterruptedException {
        id = number;
        this.clientSocket = clientSocket;
        this.tableHandler = tableHandler;

        readerObject = new ObjectInputStream(clientSocket.getInputStream());

        try {
            client = (Client) readerObject.readObject();
        } catch (ClassNotFoundException exc) {
        }

        System.out.println("Le client (" + client.getId() + ") nommé " + client.getPseudo() + " a rejoint la table " + TableHandler.getId());

        logger.info("client : " + client);
        tableHandler.addClientHandler(this);
        tableHandler.updateClient(client);

    }

    @Override
    public void run() {
        try {
            while (true) { // en écoute des maj du joueur
                try {
                    client = (Client) readerObject.readObject();
                    System.out.println("Mise à jour reçue du client (" + client.getId() + ") - " + client.getPseudo() + " de la table " + TableHandler.getId());
                    tableHandler.updateClient(client);
                } catch (EOFException e) {
                    // Aucune maj client reçue
                } catch (ClassNotFoundException e) {
                    throw new Exception("Erreur dans la désérialisation");
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Fin de la communication avec le client " + client.getPseudo(), e);
        } finally {
            try {
                clientSocket.close();
                logger.info("Client " + client.getPseudo() + " déconnecté.");
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
        return "ClientHandler{" +
                "client=" + client +
                '}';
    }
}
