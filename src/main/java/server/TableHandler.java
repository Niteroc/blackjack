package server;

import client.Client;
import table.CardSR;
import table.HandSR;
import table.TableSR;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class TableHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static int id = 1;

    private ObjectOutputStream writerObject;

    private List<ClientHandler> clientHandlerList = new ArrayList<>();

    private TableSR tbsr = new TableSR();

    private List<Client> clientList = new ArrayList<>();

    private List<Client> clientListSave = new ArrayList<>();

    private List<CardSR> cardSRList = new ArrayList<>();

    private boolean gameInProgress = false;

    private List<CardSR> cardDealerList = new ArrayList<>();

    private List<Client> currentClientList = new ArrayList<>(); // cette liste contient que les joueurs qui jouent actuellement

    public TableHandler() throws IOException {
    }

    public void addClientHandler(ClientHandler ch) {
        clientHandlerList.add(ch);
    }

    public void removeClientHandler(ClientHandler ch) {
        clientHandlerList.remove(ch);
        clientList.remove(ch.getClient());
    }

    @Override
    public void run() {
        while (true) { // en écoute des maj de la table du joueur
            try {

                int nbrGame = 0;
                /// On instancie deux cartes pour chaque joueur
                int cpt = 0;

                for(Client client : clientList){
                    if(client.hasBet() && (client.getCurrentHand() == null || client.getCurrentHand().getCardSRList().isEmpty())){
                        cpt++;
                    }
                }

                if(!gameInProgress && cpt == clientList.size() && cpt != 0){ // si égal au nombre de joueurs alors tout le monde a parié
                    gameInProgress = true;
                    logger.info("Les jeux sont faits");

                    if(nbrGame%10 == 0)setCardGame(); // on mélange les cartes tous les 10 tours
                    for(Client client : clientList){
                        drawCards(client , 2);
                    }

                    currentClientList = clientList;
                    currentClientList.get(0).setMyTurn(true);

                    /// On instancie les deux premières cartes du dealer (face cachée)
                    CardSR card = getRandomCard();
                    card.setHide(false);
                    cardDealerList.add(card);
                    card = getRandomCard();
                    card.setHide(true);
                    cardDealerList.add(card);
                }

                for(int i = 0 ; i < currentClientList.size() ; i++){
                    if(currentClientList.get(i).isWantACard()){
                        drawCards(currentClientList.get(i), 1);
                        currentClientList.get(i).setWantACard(false, false);
                    }
                    if(currentClientList.get(i).getValeur() > 21){
                        currentClientList.get(i).setCurrentBet(0);
                        currentClientList.get(i).setEndTurn(true);
                        currentClientList.get(i).setMyTurn(false);
                        currentClientList.get(i).setHasBet(false);
                        currentClientList.get(i).setValeur(0);
                    } else if (currentClientList.get(i).isWantToStay()) {
                        currentClientList.get(i).setEndTurn(true);
                        currentClientList.get(i).setMyTurn(false);
                    }
                    if(currentClientList.get(i).isEndTurn())currentClientList.get((i+1)%currentClientList.size()).setMyTurn(true);
                }

                if(!isGameInProgress() && gameInProgress){
                    for(Client client : clientList){
                        if(client.getCurrentHand() != null) client.getCurrentHand().clearCardList();
                        client.setEndTurn(false);
                        client.setMyTurn(false);
                        client.setCurrentBet(0);
                    }
                    cardDealerList.clear();
                    logger.info("Aucun joueur sur la partie actuel, arrêt de la partie.");
                    gameInProgress = false;
                }

                tbsr.setCardSRDealerList(cardDealerList);
                tbsr.setGameInProgress(gameInProgress);

                // Envoi de la table si elle a été modifiée (check des listes)
                if (areListNotEquals()) {
                    logger.info("Modification détectée\n. Ancienne liste : " + clientListSave);
                    logger.info("Nouvelle liste : " + clientList);

                    clientListSave.clear();
                    clientListSave.addAll(clientList);

                    writeObject();

                    logger.info("clientListSave mise à jour : " + clientListSave);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isGameInProgress(){
        int nbrJoueurActifs = 0;

        for(Client client : clientList){
            if(client.hasBet()){
                nbrJoueurActifs++; // hasBet est toujours vrai pour un joueur dès lors qu'il a misé et que la partie n'est pas terminée
            }else{
                nbrJoueurActifs--;
            }
        }

        return nbrJoueurActifs >= 0;
    }

    private synchronized void writeObject() throws IOException {
        for (ClientHandler clientHandler : clientHandlerList) {
            writerObject = new ObjectOutputStream(clientHandler.getClientSocket().getOutputStream());
            writerObject.writeObject(tbsr);
            writerObject.flush();
            logger.info("Table " + tbsr + " envoyée à " + clientHandler.getClient().getPseudo());
        }
    }

    public void updateClient(Client c) {
        //c.setBalance(c.getBalance()+10);
        tbsr.updateClient(c);
        clientList = tbsr.getClientList();
    }

    private boolean areListNotEquals() {

        if((clientList.size() != clientListSave.size()))return true;

        for(int i = 0 ; i < clientListSave.size() ; i++){
            if(!clientList.get(i).hasSameProperty(clientListSave.get(i))){
                return true;
            }
        }

        return false;
    }

    private void setCardGame(){
        for(int i = 0  ; i < 5 ; i++){ // 5 jeux de cartes
            for(int j = 1 ; j < 5 ; j++){
                for(int k = 1 ; k < 14 ; k++){
                    cardSRList.add(new CardSR(j,k));
                }
            }
        }
    }

    private CardSR getRandomCard(){
        int indexRandom = (int)(Math.random()*(cardSRList.size()));
        CardSR card = cardSRList.get(indexRandom);
        cardSRList.remove(indexRandom);
        logger.info("Carte tirée : " + card);
        return card;
    }

    private void drawCards(Client client, int numberToDraw){
        HandSR handSR = new HandSR();
        int value = 0;
        if(client.getValeur() != 0)value = client.getValeur();
        if(client.getCurrentHand() != null)handSR = client.getCurrentHand();
        for(int i = 0 ; i < numberToDraw ; i++){
            CardSR cardSR = getRandomCard();
            handSR.addCardToList(cardSR);
            value += cardSR.getValue();
        }
        client.setCurrentHand(handSR);
        client.setValeur(value);
    }

    public static int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TableHandler - id:" +id + "{" +
                "tbsr=" + tbsr +
                '}';
    }
}