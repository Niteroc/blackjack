package server;

import client.Client;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private int id = 0;

    private static int number = 0;

    public Socket getClientSocket() {
        return clientSocket;
    }

    private final Socket clientSocket;

    private Client clientSave = null;
    private Client client = null;

    private ObjectInputStream readerObject;
    private ObjectOutputStream writeObject;

    private TableHandler tableHandler;

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());


    public ClientHandler(Socket clientSocket, TableHandler tableHandler) throws IOException {
        id = ++number;
        this.clientSocket = clientSocket;
        this.tableHandler = tableHandler;

        writeObject = new ObjectOutputStream(clientSocket.getOutputStream());
        readerObject = new ObjectInputStream(clientSocket.getInputStream());

        try {
            client = (Client) readerObject.readObject();
        } catch (ClassNotFoundException exc) {
        }

        System.out.println("Le client (" + id + ") nommé " + client.getPseudo() + " a rejoint la table " + tableHandler.getId());

        tableHandler.addClientHandler(this);
        tableHandler.updateClient(client);
    }

    @Override
    public void run() {
        try {
            while (true) { // en écoute des maj du joueur
                clientSave = client;
                client = null;
                try {
                    client = (Client) readerObject.readObject();
                    System.out.println("Mise à jour reçue du client (" + id + ") - " + client.getPseudo() + " de la table " + tableHandler.getId());
                    tableHandler.updateClient(client);
                } catch (EOFException e) {
                    // Aucune maj client reçue
                } catch (ClassNotFoundException e) {
                    throw new Exception("Erreur dans la désérialisation");
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Fin de la communication avec le client " + clientSave.getPseudo(), e);
        } finally {
            try {
                clientSocket.close();
                logger.info("Client " + clientSave.getPseudo() + " déconnecté.");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erreur lors de la déconnexion du client ", e);
            }
        }
    }
}
