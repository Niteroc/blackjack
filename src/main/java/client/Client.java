package client;

import gui.GUI;
import table.TableSR;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Serializable {
    public String getId() {
        return id;
    }

    private static final long serialVersionUID = 1L;

    private final String id = UUID.randomUUID().toString();
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public String getPseudo() {
        return pseudo;
    }

    private String pseudo = "";

    private int balance = 0;
    private transient ObjectOutputStream writerObject;
    private transient ObjectInputStream readerObject;
    private transient Socket socket; // Ajoutez une variable membre pour le socket

    private transient GUI gui;

    public Client(Socket socket) throws IOException {

        do {
            pseudo = JOptionPane.showInputDialog("Saisissez votre pseudo");
        } while ((pseudo == null) || (pseudo.isEmpty()));

        this.socket = socket;
        writerObject = new ObjectOutputStream(socket.getOutputStream());

        try {

            SwingUtilities.invokeLater(() -> {
                gui = new GUI();  // Instanciation de GUI et stockage de la référence
            });

            sendClient(); // premier envoi pour s'initialiser

            TableSR tbsr;

            Thread.sleep(1000); // on attend une seconde pour être sûr que la table soit envoyée

            while (true) {
                try {
                    logger.info("En attente d'une nouvelle table");
                    try{
                        // Lecture de la table
                        tbsr = readTable();
                        logger.info("Maj de la table " + tbsr.getTableHandlerId());

                        // Mise à jour du client courant via la table reçue
                        reaffectAllStatus(tbsr.getClientModification(this));

                        // Mise à jour des composants graphiques
                        refreshTable();
                        gui.testText(tbsr);
                        logger.info("Rafraichissement de la vue");

                        // Envoi du client courant au serveur
                        sendClient();
                    }catch(Exception e){
                        // ne rien faire -- skip la lecture
                    }

                } catch (Exception exc) {
                    logger.log(Level.SEVERE, "erreur", exc);
                }
            }

        } catch (Exception e) {
            System.out.println("exception: " + e);
            System.out.println("closing...");
            try {
                socket.close();
            } catch (java.net.ConnectException e2) {
                System.out.println("no server running");
                System.exit(5);
            } catch (IOException e3) {
                System.out.println("no open socket");
                System.exit(6);
            }
        }

    }

    private void reaffectAllStatus(Client clientModified) {
        logger.info("avant " + this);
        logger.info("après " + clientModified);
        pseudo = clientModified.getPseudo();
        balance = clientModified.getBalance();
    }

    public static void main(String[] args) {
        try {

            Socket socket = null;
            try {
                InetAddress addr = InetAddress.getByName(null);
                System.out.println("addr = " + addr);
                socket = new Socket(addr, 12345);
                System.out.println("socket = " + socket);
            } catch (Exception nos) {
                System.out.println("no server running");
                System.exit(5);
            }

            new Client(socket);

            try {
                socket.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erreur de communication avec le serveur", e);
            }
        } catch (Exception e) {

        }
    }

    private void sendClient() throws IOException {
        writerObject.writeObject(this);
        writerObject.reset();
    }

    private TableSR readTable() throws IOException, ClassNotFoundException {
        readerObject = new ObjectInputStream(socket.getInputStream());
        return (TableSR) readerObject.readObject();
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", pseudo='" + pseudo + '\'' +
                ", balance=" + balance +
                '}';
    }

    private static void refreshTable() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id.equals(client.id);
    }

    public boolean hasSameProperty(Client c) {
        return balance == c.balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo);
    }
}
