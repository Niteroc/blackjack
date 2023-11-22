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

/**
 * Classe représentant un client du jeu de Blackjack.
 */
public class Client implements Serializable, Runnable, Cloneable {
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

    private HandSR currentHand = new HandSR();

    private boolean hasBet = false;

    private boolean myTurn = false;

    private boolean endTurn = false;

    private boolean wantACard = false;

    private boolean wantToStay = false;

    private int gain = -1;

    /**
     * Méthode pour démarrer l'exécution du client.
     */
    public void run() {
        try {

            while (pseudo.isEmpty()) { // on attend que le pseudo soit défini pour poursuivre
                Thread.sleep(100);
            }

            writerObject = new ObjectOutputStream(socket.getOutputStream());

            // On charge les valeurs du client, si elles avaient été sauvegardées
            Client client = Server.findInList(savedClientList, this);
            reaffectAllStatus(client);

            Thread.sleep(2000);

            sendClient(); // premier envoi pour s'initialiser

            TableSR tbsr;

            Thread.sleep(1000); // on attend une seconde pour être sûr que la table soit envoyée

            while (true) {
                try {
                    logger.info("En attente d'une nouvelle table");
                    try {
                        // Lecture de la table
                        tbsr = readTable();
                        logger.info("Maj de la table reçu " + tbsr);

                        // Mise à jour du client courant via la table reçue
                        reaffectAllStatus(tbsr.getClientModification(this));

                        // Mise à jour des composants graphiques
                        controller.testText(tbsr);
                        logger.info("Rafraichissement de la vue");

                    } catch (Exception e) {
                        System.exit(0);
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

    /**
     * Méthode pour arrêter le client et fermer la connexion.
     */
    public void shutdown() {
        System.exit(0);
    }

    /**
     * Constructeur de la classe Client.
     * Initialise le client et établit une connexion avec le serveur.
     *
     * @param controller Le contrôleur de l'interface graphique associée au client.
     */
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

            try {
                // Lecture de la liste des clients déjà connectés
                ObjectInputStream readerObjectList = new ObjectInputStream(socket.getInputStream());
                connectedClientList = (List<Client>) readerObjectList.readObject();

                // Lecture de la liste des clients sauvegardés au préalable
                readerObjectList = new ObjectInputStream(socket.getInputStream());
                savedClientList = (List<Client>) readerObjectList.readObject();
            } catch (Exception e) {
                // ne rien faire -- skip la lecture
            }

            try {
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

                for (Client client : savedClientList) {
                    existingPseudos.addItem(client.getPseudo());
                }

                inputPanel.add(existingPseudoLabel);
                inputPanel.add(existingPseudos);

                JButton validateButton = new JButton("Valider");
                JButton closeButton = new JButton("Fermer");

                validateButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (isAlreadyConnected(pseudoField.getText())) {
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
            } catch (Exception e) {
                logger.severe("Erreur sélection des pseudos : " + e.getMessage());
            }

        } catch (Exception nos) {
            System.out.println("Le serveur n'est pas joignable");
            System.exit(5);
        }
    }

    private boolean isAlreadyConnected(String pseudo) {
        for (Client client : connectedClientList) {
            if (client.getPseudo().equals(pseudo)) return true;
        }
        return false;
    }

    // Getters et Setters avec des commentaires Javadoc pour chaque méthode

    @Override
    public Client clone() {
        try {
            return (Client) super.clone();
        } catch (CloneNotSupportedException e) {
            // Gestion de l'exception si le clonage n'est pas supporté
            return null;
        }
    }

    /**
     * Réaffecte tous les états du client avec ceux du client modifié.
     *
     * @param clientModified Le client contenant les états modifiés à réaffecter.
     */
    private void reaffectAllStatus(Client clientModified) {
        pseudo = clientModified.getPseudo();
        balance = clientModified.getBalance();
        gain = clientModified.getGain();
        currentHand = clientModified.getCurrentHand();
        hasBet = clientModified.hasBet();
        myTurn = clientModified.isMyTurn();
        endTurn = clientModified.isEndTurn();
        wantACard = clientModified.isWantACard();
        wantToStay = clientModified.isWantToStay();
    }

    /**
     * Envoie les informations du client à la table.
     *
     * @throws IOException En cas d'erreur lors de l'envoi des informations.
     */
    private void sendClient() throws IOException {
        logger.info("Envoi du client à la table : " + this);
        writerObject.writeObject(this);
        writerObject.reset();
    }

    /**
     * Lit la table envoyée par le serveur.
     *
     * @return La table reçue du serveur.
     * @throws IOException            En cas d'erreur lors de la lecture de la table.
     * @throws ClassNotFoundException Si la classe de l'objet reçu n'est pas trouvée.
     */
    private TableSR readTable() throws IOException, ClassNotFoundException {
        ObjectInputStream readerObject = new ObjectInputStream(socket.getInputStream());
        return (TableSR) readerObject.readObject();
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance, boolean toSend) throws IOException {
        this.balance = balance;
        if (toSend) sendClient();
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    public int getCurrentBet() {
        return currentBet;
    }

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
        if (toSend) sendClient();
    }

    public boolean canBet(int bet){
        return getBalance() > bet;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public boolean isEndTurn() {
        return endTurn;
    }

    public void setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
    }

    public boolean isWantACard() {
        return wantACard;
    }

    public void setWantACard(boolean wantACard, boolean toSend) throws IOException {
        this.wantACard = wantACard;
        if (toSend) sendClient();
    }

    public boolean isWantToStay() {
        return wantToStay;
    }

    public void setWantToStay(boolean wantToStay, boolean toSend) throws IOException {
        this.wantToStay = wantToStay;
        if (toSend) sendClient();
    }

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    @Override
    public String toString() {
        return "Client{" + "id='" + id + '\'' + ", pseudo='" + pseudo + '\'' + ", balance=" + balance + ", currentBet=" + currentBet + ", currentHand=" + currentHand + ", hasBet=" + hasBet + ", gain=" + gain + ", myTurn=" + myTurn + ", endTurn=" + endTurn + ", wantACard=" + wantACard + ", wantToStay=" + wantToStay + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id.equals(client.id);
    }

    /**
     * Vérifie si le client possède les mêmes propriétés qu'un autre client.
     *
     * @param c Le client avec lequel comparer les propriétés.
     * @return true si les propriétés sont identiques, sinon false.
     */
    public boolean hasSameProperty(Client c) {
        return (balance == c.balance && hasBet == c.hasBet && currentHand.equals(c.currentHand) && wantACard == c.wantACard && endTurn == c.endTurn && wantToStay == c.wantToStay && myTurn == c.myTurn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pseudo);
    }

}
