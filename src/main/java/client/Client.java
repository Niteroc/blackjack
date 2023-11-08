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

    private static BufferedReader reader = null;
    private static PrintWriter writer = null;
    private static ObjectOutputStream writerObject = null;
    private static ObjectInputStream readerObject = null;
    private static transient Socket socket; // Ajoutez une variable membre pour le socket

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
                String server = null;
                InetAddress addr = InetAddress.getByName(server);
                System.out.println("addr = " + addr);
                socket = new Socket(addr, 12345);
                System.out.println("socket = " + socket);
            } catch (Exception nos) {
                System.out.println("no server running");
                System.exit(5);
            }

            Client c = new Client(socket);

            SwingUtilities.invokeLater(GUI::new);

            sendClient(c);

            System.out.println("1");
            while (true) {
                TableSR tbsr = null;
                try {
                    System.out.println("2");
                    tbsr = readTable();
                    System.out.println("here");
                    // c = tbsr.getCurrentClient(socket.getID);
                    /// Extraction de moi-même

                    refreshTable();

                    sendClient(c);

                } catch (Exception exc) {
                    logger.log(Level.SEVERE, "erreur", exc);
                }
                if (tbsr == null) break;

            }

            System.out.println("closing terminal...");

            try {
                socket.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erreur de communication avec le serveur", e);
            }
        } catch (Exception e) {

        }
    }

    private static void sendClient(Client c) throws IOException, InterruptedException {
        writerObject.writeObject(c);
        Thread.sleep(2000);
    }

    private static TableSR readTable() throws IOException, ClassNotFoundException {
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
