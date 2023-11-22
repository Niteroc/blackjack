package table;

import client.Client;
import server.ClientHandler;
import server.TableHandler;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Cette classe représente l'état d'une table de jeu.
 */
public class TableSR implements Serializable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static final long serialVersionUID = 1L;

    private boolean isGameInProgress = false;

    private HandSR handDealer = new HandSR();

    private List<Client> clientList = new ArrayList<>();

    /**
     * Constructeur par défaut de la classe TableSR.
     */
    public TableSR() {
    }

    /**
     * Renvoie la liste des clients de la table.
     *
     * @return La liste des clients de la table.
     */
    public List<Client> getClientList() {
        return clientList;
    }

    /**
     * Modifie l'état de la partie en cours ou non.
     *
     * @param gameInProgress État de la partie en cours ou non.
     */
    public void setGameInProgress(boolean gameInProgress) {
        isGameInProgress = gameInProgress;
    }

    /**
     * Vérifie si une partie est en cours sur cette table.
     *
     * @return true si une partie est en cours, sinon false.
     */
    public boolean isGameInProgress() {
        return isGameInProgress;
    }

    /**
     * Récupère un client de la table pour une modification ultérieure.
     *
     * @param client Le client dont on souhaite obtenir une copie de référence.
     * @return Le client de la table correspondant ou le client lui-même s'il n'est pas présent dans la table.
     */
    public Client getClientModification(Client client) {
        for (Client client1 : clientList) {
            if (client1.equals(client)) return client1;
        }
        return client;
    }

    /**
     * Renvoie la main du croupier.
     *
     * @return La main du croupier.
     */
    public HandSR getHandDealer() {
        return handDealer;
    }

    /**
     * Définit la main du croupier.
     *
     * @param handDealer La main du croupier à définir.
     */
    public void setHandDealer(HandSR handDealer) {
        this.handDealer = handDealer;
    }

    /**
     * Renvoie une représentation textuelle de l'objet TableSR.
     *
     * @return Une chaîne de caractères représentant l'objet TableSR.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("TableSR{\n");

        for (Client client : clientList) {
            stringBuilder.append("\t").append(client).append('\n');
        }

        stringBuilder.append("\t").append(handDealer).append('\n');

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    /**
     * Met à jour un client dans la table ou l'ajoute s'il n'existe pas encore.
     *
     * @param c Le client à mettre à jour ou à ajouter dans la table.
     */
    public void updateClient(Client c) {
        logger.info(c.getPseudo() + " inséré/maj dans la table " + TableHandler.getId() + " // déjà existant : " + clientList.contains(c));
        if (!clientList.contains(c)) {
            clientList.add(c);
        } else {
            clientList.set(clientList.indexOf(c), c);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableSR tableSR = (TableSR) o;
        return Objects.equals(clientList, tableSR.clientList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientList);
    }
}
