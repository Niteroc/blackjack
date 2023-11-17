package client;

import gui.Controller;
import server.Server;
import table.HandSR;
import table.TableSR;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Serializable, Runnable {
    public String getId() {
        return id;
    }

    private static final long serialVersionUID = 1L;

    private final String id = UUID.randomUUID().toString();
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private static List<Client> connectedClientList;

    private static List<Client> savedClientList;

    public String getPseudo() {
        return pseudo;
    }

    private String pseudo = "";

    private int balance = 0;
    private int currentBet = 0;
    private transient ObjectOutputStream writerObject;
    private transient Socket socket; // Ajoutez une variable membre pour le socket

    private transient Controller controller;

    private HandSR currentHand;

    private boolean hasBet = false;

    public void run() {
        try {

            while (pseudo.isEmpty()) { // on attend que le pseudo soit déifni pour poursuivre
                try {
                    Thread.sleep(100); // Ajoute un court délai pour ne pas surcharger le processeur
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            writerObject = new ObjectOutputStream(socket.getOutputStream());

            /*SwingUtilities.invokeLater(() -> {
                gui = new GUI(this);  // Instanciation de GUI et stockage de la référence
            });*/

            // On charge les valeurs du client, si elles avaient été sauvegardées
            Client client = Server.findInList(savedClientList, this);
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
                        controller.testText(tbsr);
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
        }
    }

    public void shutdown(){
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    public Client(Controller controller) {

        try {
            this.controller = controller;
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
            int port = 12345;
            System.out.println("Adresse = " + addr + ":" + port);
            socket = new Socket(addr, port);
            System.out.println("Socket = " + socket);

            try{
                // Lecture de la liste des clients déjà connectés
                ObjectInputStream readerObjectList = new ObjectInputStream(socket.getInputStream());
                connectedClientList = (List<Client>) readerObjectList.readObject();

                // Lecture de la liste des clients sauvegardés au préalable
                readerObjectList = new ObjectInputStream(socket.getInputStream());
                savedClientList = (List<Client>) readerObjectList.readObject();
            }catch(Exception e){
                // ne rien faire -- skip la lecture
            }

            try{
                JTextField pseudoField;
                JComboBox<String> existingPseudos;

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

                for(Client client : savedClientList){
                    existingPseudos.addItem(client.getPseudo());
                }

                inputPanel.add(existingPseudoLabel);
                inputPanel.add(existingPseudos);

                JButton validateButton = new JButton("Valider");
                JButton closeButton = new JButton("Fermer");

                validateButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(isAlreadyConnected(pseudoField.getText())){
                            JOptionPane.showMessageDialog(null, "Pseudo déjà connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                        if (!pseudoField.getText().isEmpty() && !isAlreadyConnected(pseudoField.getText())) {
                            pseudo = pseudoField.getText();
                            frame.dispose();
                        }
                    }
                });

                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        System.exit(0);
                    }
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(validateButton);
                buttonPanel.add(closeButton);

                frame.add(inputPanel);
                frame.add(buttonPanel);

                // Mettre à jour le champ de texte lorsque la sélection de la liste change
                existingPseudos.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pseudoField.setText((String) existingPseudos.getSelectedItem());
                    }
                });

                frame.setVisible(true);
            }catch(Exception e){
                logger.severe("Erreur sélection des pseudos : " + e.getMessage());
            }

        } catch (Exception nos) {
            System.out.println("Le serveur n'est pas joignable");
            System.exit(5);
        }
    }

    private boolean isAlreadyConnected(String pseudo){
        for(Client client : connectedClientList){
            if(client.getPseudo().equals(pseudo))return true;
        }
        return false;
    }

    private void reaffectAllStatus(Client clientModified) {
        logger.info("avant " + this);
        logger.info("après " + clientModified);
        pseudo = clientModified.getPseudo();
        balance = clientModified.getBalance();
        currentHand = clientModified.getCurrentHand();
        hasBet = clientModified.hasBet();
    }

    private void sendClient() throws IOException {
        logger.info("Envoi du client à la table");
        writerObject.writeObject(this);
        writerObject.reset();
    }

    private TableSR readTable() throws IOException, ClassNotFoundException {
        ObjectInputStream readerObject = new ObjectInputStream(socket.getInputStream());
        return (TableSR) readerObject.readObject();
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) throws IOException {
        this.balance = balance;
        sendClient();
    }

    public void setCurrentBet(int currentBet) throws IOException {
        this.currentBet = currentBet;
    }

    public int getCurrentBet() { return currentBet; }

    public void setCurrentHand(HandSR currentHand) {
        this.currentHand = currentHand;
    }

    public HandSR getCurrentHand() {
        return currentHand;
    }

    public boolean hasBet() {
        return hasBet;
    }

    public void setHasBet(boolean hasBet, int currentBet, boolean toSend) throws IOException {
        this.setCurrentBet(currentBet);
        this.hasBet = hasBet;
        if(toSend)sendClient();
    }

    public void setHasBet(boolean hasBet) throws IOException {
        this.hasBet = hasBet;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", pseudo='" + pseudo + '\'' +
                ", balance=" + balance +
                ", currentBet=" + currentBet +
                ", currentHand=" + currentHand +
                ", hasBet=" + hasBet +
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
        return (balance == c.balance && hasBet == c.hasBet && currentHand.equals(c.currentHand));
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo);
    }
}
