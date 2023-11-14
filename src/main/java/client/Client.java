package client;

import gui.GUI;
import qrcode.QrCode;
import server.ClientHandler;
import table.TableSR;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static int port = 12345;

    public Client(Socket socket)  {

        JTextField pseudoField;
        JComboBox<Client> existingPseudos;

        try {

            JFrame frame = new JFrame("Saisie de Pseudo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

            JLabel pseudoLabel = new JLabel("Pseudo:");
            pseudoField = new JTextField();
            inputPanel.add(pseudoLabel);
            inputPanel.add(pseudoField);

            JLabel existingPseudoLabel = new JLabel("Pseudos existants:");
            existingPseudos = new JComboBox<>();

            for(Client client : ClientHandler.loadClientsList()){
                existingPseudos.addItem(client);
            }

            inputPanel.add(existingPseudoLabel);
            inputPanel.add(existingPseudos);

            JButton validateButton = new JButton("Valider");
            JButton closeButton = new JButton("Fermer");

            validateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!pseudoField.getText().isEmpty()) {
                        pseudo = pseudoField.getText();
                        frame.dispose();
                    }
                }
            });

            closeButton.addActionListener(e -> frame.dispose());

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(validateButton);
            buttonPanel.add(closeButton);

            frame.add(inputPanel);
            frame.add(buttonPanel);

            // Mettre à jour le champ de texte lorsque la sélection de la liste change
            existingPseudos.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Client client = (Client) existingPseudos.getSelectedItem();
                    if(client != null)pseudoField.setText(client.getPseudo());
                }
            });

            frame.setVisible(true);

            while (pseudo.isEmpty()) {
                try {
                    Thread.sleep(100); // Ajoute un court délai pour ne pas surcharger le processeur
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            this.socket = socket;
            writerObject = new ObjectOutputStream(socket.getOutputStream());

            SwingUtilities.invokeLater(() -> {
                gui = new GUI();  // Instanciation de GUI et stockage de la référence
            });

            // On charge les valeurs du client, si elles avaient sauvegardées
            Client client = ClientHandler.findInList(ClientHandler.loadClientsList(), this);
            reaffectAllStatus(client);

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
            Socket socket;
            String host;

            // Expression régulière pour une adresse IP
            String ipRegex = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
            Pattern pattern = Pattern.compile(ipRegex);
            Matcher matcher;

            do {
                host = JOptionPane.showInputDialog("Saisissez l'ip (192.168.xxx.xxx)");
                matcher = pattern.matcher(host);
            } while (host.isEmpty() || !matcher.matches());

            InetAddress addr = InetAddress.getByName(host);
            System.out.println("Adresse = " + addr + ":" + port);
            socket = new Socket(addr, port);
            System.out.println("Socket = " + socket);

            new Client(socket);
        } catch (Exception nos) {
            System.out.println("Le serveur n'est pas joignable");
            System.exit(5);
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
        return pseudo + " | " + balance + "€";
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
