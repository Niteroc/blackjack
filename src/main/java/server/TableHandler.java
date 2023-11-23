package server;

import client.Client;
import table.CardSR;
import table.HandSR;
import table.TableSR;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Gère les interactions entre les clients, les mains et les cartes sur la table de Blackjack.
 */
public class TableHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static final int id = 1;

    private final List<ClientHandler> clientHandlerList = new ArrayList<>();

    private final TableSR tbsr = new TableSR();

    private List<Client> clientList = new ArrayList<>();

    private final List<Client> clientListSave = new ArrayList<>();

    private final List<CardSR> cardSRList = new ArrayList<>();

    private boolean gameInProgress = false;

    private boolean hasAllPlayed = false;

    private boolean hasDealerPlayed = false;

    private boolean hasDealerShowTheSecondCard = false;

    private final HandSR dealerHand = new HandSR();

    private HandSR dealerHandSave = new HandSR();

    private int nbrGame = 0;

    private final List<Client> currentClientList = new ArrayList<>(); // cette liste contient que les joueurs qui jouent actuellement

    private final Object lock = new Object();
    private boolean sendTableLaunched = false;

    private boolean endGame = false;

    /**
     * Initialise un gestionnaire de table.
     *
     * @throws IOException En cas d'erreur d'entrée/sortie.
     */
    public TableHandler() throws IOException {
    }

    /**
     * Méthode exécutée lors du démarrage du thread de la table.
     */
    @Override
    public void run() {
        while (true) {
            try {

                synchronized (lock) {
                    while (sendTableLaunched) {
                        lock.wait(); // Met le thread en attente jusqu'à ce que sendTable() soit appelé
                    }
                }

                if (gameInProgress) {
                    // Phase où la partie est encore en cours mais les joueurs ont tous finis
                    if (!hasActivePlayer()) {
                        for (Client client : currentClientList) {
                            // gain
                            if (client.getCurrentHand().getValue() <= 21) {
                                if (client.getCurrentHand().getValue() > dealerHand.getValue() || dealerHand.getValue() > 21) {
                                    if (client.getCurrentHand().isABlackJack()) {
                                        client.setGain((int) (client.getCurrentBet() * 2.5));
                                    } else {
                                        client.setGain(client.getCurrentBet() * 2);
                                    }
                                } else if ((client.getCurrentHand().getValue() == dealerHand.getValue())) {
                                    client.setGain(client.getCurrentBet());
                                } else {
                                    client.setGain(0);
                                }
                            } else {
                                client.setGain(0);
                            }

                            if (client.getCurrentHand() != null) client.getCurrentHand().clearCardList();
                            client.setEndTurn(false);
                            client.setMyTurn(false);
                            client.setCurrentBet(0);
                            client.setBalance(client.getBalance() + client.getGain(), false);
                        }

                        dealerHand.clearCardList();
                        dealerHandSave = dealerHand.clone();

                        logger.info("Fin de la partie. \n En attente de nouvelles mises.");
                        logger.info("Faites vos jeux");

                        gameInProgress = false;
                        hasAllPlayed = false;
                        hasDealerPlayed = false;
                        hasDealerShowTheSecondCard = false;
                        nbrGame++;
                        endGame = true;
                        currentClientList.clear();
                    }

                    for (Client client : currentClientList) {
                        if (hasDealerPlayed) {
                            client.setHasBet(false, client.getCurrentBet(), false);
                        }
                    }

                    if (hasDealerShowTheSecondCard) {
                        while (shouldHit(dealerHand)) {
                            dealerHand.addCardToList(getRandomCard());
                        }
                        hasDealerPlayed = true;
                    }

                    if (hasAllPlayed) {
                        dealerHand.getCardSRList().get(1).setHide(false);
                        hasDealerShowTheSecondCard = true;
                    }

                    tbsr.setHandDealer(dealerHand);
                    tbsr.setGameInProgress(gameInProgress);
                    tbsr.setEndGame(endGame);

                    needToSend(false);

                }

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Envoie la table aux clients connectés.
     */
    public void sendTable(boolean force) {
        try {

            int cpt = 0;

            // On compte le nombre de client qui ont misé
            for (Client client : clientList) {
                if (client.hasBet() && (client.getCurrentHand() == null || client.getCurrentHand().getCardSRList().isEmpty())) {
                    cpt++;
                }
            }

            // Si tous les joueurs ont misés, on leur attribue les deux premières cartes
            if (!gameInProgress && cpt == clientList.size() && cpt != 0) { // si égal au nombre de joueurs alors tout le monde a parié

                gameInProgress = true;
                endGame = false;
                logger.info("Les jeux sont faits");
                logger.info("Rien ne va plus");

                if (nbrGame % 10 == 0) setCardGame(); // on mélange les cartes tous les 10 tours
                for (Client client : clientList) {
                    drawCards(client, 2);
                    client.setGain(0);
                }

                currentClientList.clear();
                currentClientList.addAll(clientList);
                clientList.get(0).setMyTurn(true);

                /// On instancie les deux premières cartes du dealer
                CardSR card = getRandomCard();
                dealerHand.addCardToList(card);
                card = getRandomCard();
                card.setHide(true); // face cachée
                dealerHand.addCardToList(card);
                dealerHandSave = dealerHand.clone();
            }

            // Action dans la partie, tout à tour
            if (gameInProgress) {
                for (int i = 0; i < currentClientList.size(); i++) {
                    if (currentClientList.get(i).isEndTurn()) {
                        currentClientList.get(i).setEndTurn(false);
                        currentClientList.get((i + 1) % currentClientList.size()).setMyTurn(true);
                    }
                    if (currentClientList.get(i).isWantACard()) {
                        drawCards(currentClientList.get(i), 1);
                        currentClientList.get(i).setWantACard(false, false);
                    }
                    if (currentClientList.get(i).getCurrentHand().getValue() > 21) {
                        currentClientList.get(i).setEndTurn(true);
                        currentClientList.get(i).setMyTurn(false);
                        if (currentClientList.get(i) == currentClientList.get(currentClientList.size() - 1)) {
                            hasAllPlayed = true;
                            break;
                        }
                    } else if (currentClientList.get(i).isWantToStay()) {
                        currentClientList.get(i).setEndTurn(true);
                        currentClientList.get(i).setMyTurn(false);
                        currentClientList.get(i).setWantToStay(false, false);
                        if (currentClientList.get(i) == currentClientList.get(currentClientList.size() - 1)) {
                            hasAllPlayed = true;
                            break;
                        }
                    }
                    if (currentClientList.get(i).isEndTurn()) {
                        currentClientList.get(i).setEndTurn(false);
                        currentClientList.get((i + 1) % currentClientList.size()).setMyTurn(true);
                    }
                }
            }

            tbsr.setHandDealer(dealerHand);
            tbsr.setGameInProgress(gameInProgress);
            tbsr.setEndGame(endGame);

            // Envoi de la table si elle a été modifiée (check des listes et dealer)
            needToSend(force);

            Thread.sleep(1000);

        } catch (Exception ignored) {

        }
        finally {
            synchronized (lock) {
                sendTableLaunched = false;
                lock.notify(); // Réveille le thread s'il est en attente
            }
        }
    }

    /**
     * Détermine si le dealer devrait tirer une nouvelle carte selon les règles du Blackjack.
     *
     * @param hand La main du dealer.
     * @return true si le dealer doit tirer une carte supplémentaire, sinon false.
     */
    public boolean shouldHit(HandSR hand) {
        int handValue = hand.getValue();
        boolean soft17 = hand.containsAce() && (handValue == 17); // Vérifie si c'est un soft 17 (Un as + une main valant 6)

        return handValue < 17 || soft17;
    }

    /**
     * Vérifie s'il y a des joueurs actifs sur la table.
     *
     * @return true s'il y a au moins un joueur actif, sinon false.
     */
    private boolean hasActivePlayer() {
        int nbrJoueurActifs = 0;

        for (Client client : currentClientList) {
            if (client.hasBet()) {
                nbrJoueurActifs++; // hasBet est toujours vrai pour un joueur dès lors qu'il a misé et que la partie n'est pas terminée
            } else {
                nbrJoueurActifs--;
            }
        }

        return nbrJoueurActifs > 0;
    }

    /**
     * Envoie la table mise à jour aux clients connectés.
     *
     * @throws IOException En cas d'erreur d'entrée/sortie lors de l'écriture de la table vers les clients.
     * @throws InterruptedException En cas d'interruption du thread pendant l'envoi de la table.
     */
    private synchronized void writeObject() throws IOException, InterruptedException {
        for (ClientHandler clientHandler : clientHandlerList) {
            ObjectOutputStream writerObject = new ObjectOutputStream(clientHandler.getClientSocket().getOutputStream());
            writerObject.writeObject(tbsr);
            writerObject.flush();
            logger.info("Table " + tbsr + " envoyée à " + clientHandler.getClient().getPseudo());
        }
        Thread.sleep(2000);
    }

    /**
     * Met à jour un client spécifique sur la table.
     *
     * @param c Le client à mettre à jour.
     * @throws InterruptedException En cas d'interruption du thread lors de la mise à jour du client.
     */
    public synchronized void updateClient(Client c, boolean firstConnection) throws InterruptedException {

        synchronized (lock) {
            sendTableLaunched = true;
        }

        tbsr.updateClient(c);
        clientList = tbsr.getClientList();

        for(Client client : currentClientList){
            client = clientList.get(clientList.indexOf(client));
            currentClientList.set(currentClientList.indexOf(client),client);
        }
        Thread.sleep(2000);
        sendTable(firstConnection);
    }

    /**
     * Vérifie s'il est nécessaire d'envoyer une mise à jour de la table aux clients connectés.
     *
     * @throws IOException En cas d'erreur d'entrée/sortie lors de la vérification et de l'envoi de la table.
     * @throws InterruptedException En cas d'interruption du thread pendant l'envoi de la table.
     */
    private synchronized void needToSend(boolean force) throws IOException, InterruptedException {

        boolean needToSend = false;

        if(currentClientList.size() == clientListSave.size()){
            for (int i = 0; i < clientListSave.size(); i++) {
                if (!currentClientList.get(i).hasSameProperty(clientListSave.get(i))) {
                    needToSend = true;
                    break;
                }
            }

            if (!dealerHand.equals(dealerHandSave)) {
                needToSend = true;
            }
        }

        if(currentClientList.size() != clientListSave.size()) {
            needToSend = true;
        }

        if(!gameInProgress) needToSend = true;

        if (needToSend || force) {
            // On récupère les clients sans leurs références
            clientListSave.clear();
            for (Client client : currentClientList) {
                clientListSave.add(client.clone());
            }

            // On récupère la main du dealer sans sa référence
            dealerHandSave = dealerHand.clone();

            writeObject();
        }
    }

    private void setCardGame() {
        for (int i = 0; i < 5; i++) { // 5 jeux de cartes
            for (int j = 1; j < 5; j++) {
                for (int k = 1; k < 14; k++) {
                    cardSRList.add(new CardSR(j, k));
                }
            }
        }
    }

    /**
     * Tire une carte aléatoire du jeu de cartes disponibles.
     *
     * @return La carte tirée aléatoirement.
     */
    private CardSR getRandomCard() {
        int indexRandom = (int) (Math.random() * (cardSRList.size()));
        CardSR card = cardSRList.get(indexRandom);
        cardSRList.remove(indexRandom);
        logger.info("Carte tirée : " + card);
        return card;
    }

    /**
     * Distribue des cartes à un joueur.
     *
     * @param client       Le client qui reçoit les cartes.
     * @param numberToDraw Le nombre de cartes à distribuer.
     */
    private void drawCards(Client client, int numberToDraw) {
        for (int i = 0; i < numberToDraw; i++) {
            CardSR cardSR = getRandomCard();
            client.getCurrentHand().addCardToList(cardSR);
        }
    }

    public static int getId() {
        return id;
    }

    public void addClientHandler(ClientHandler ch) {
        clientHandlerList.add(ch);
    }

    public void removeClientHandler(ClientHandler ch) {
        clientHandlerList.remove(ch);
        clientList.remove(ch.getClient());
    }

    @Override
    public String toString() {
        return "TableHandler - id:" + id + "{" + "tbsr=" + tbsr + '}';
    }
}