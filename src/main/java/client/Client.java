package client;

import gui.GUI;
import table.TableSR;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Serializable {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public String getPseudo() {
        return pseudo;
    }

    private String pseudo = "";

    private transient BufferedReader reader = null;
    private transient PrintWriter writer = null;
    private transient ObjectOutputStream writerObject = null;
    private transient ObjectInputStream readerObject = null;
    private transient Socket socket; // Ajoutez une variable membre pour le socket

    private Client clientSave;

    public Client(Socket socket) {

        do {
            pseudo = JOptionPane.showInputDialog("Saisissez votre pseudo");
        } while ((pseudo == null) || (pseudo.isEmpty()));

        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            readerObject = new ObjectInputStream(socket.getInputStream());
            writerObject = new ObjectOutputStream(socket.getOutputStream());

            SwingUtilities.invokeLater(GUI::new);

            sendClient(this); // premier envoi pour s'initialiser

            pseudo += "j";

            TableSR tbsr;

            Thread.sleep(1000); // on attend une seconde pour être sûr que la table soit envoyée

            while (true) {
                try {
                    clientSave = this;
                    logger.info("En attente d'une nouvelle table");
                    try{
                        tbsr = readTable();
                        logger.info("Maj de la table " + tbsr.getTableHandlerId());
                        refreshTable();
                        logger.info("Rafraichissement de la vue");
                    }catch(Exception e){
                        logger.info(e.getMessage());
                        // ne rien faire -- skip la lecture
                    }

                    //if(!clientSave.equals(this))sendClient(this); // le client a été modifié

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

            Client c = new Client(socket);

            try {
                socket.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erreur de communication avec le serveur", e);
            }
        } catch (Exception e) {

        }
    }

    private synchronized void sendClient(Client c) throws IOException {
        writerObject.writeObject(c);
        writerObject.flush();
    }

    private synchronized TableSR readTable() throws IOException, ClassNotFoundException {
        readerObject = new ObjectInputStream(socket.getInputStream());
        return (TableSR) readerObject.readObject();
    }

    @Override
    public String toString() {
        return " Client(" + pseudo + ")";
    }

    private static void refreshTable() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(pseudo, client.pseudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo);
    }
}
