package server;

import client.Client;
import table.TableSR;

import java.io.*;
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


    public TableHandler() throws IOException {
    }

    public void addClientHandler(ClientHandler ch) {
        clientHandlerList.add(ch);
    }

    public void removeClientHandler(ClientHandler ch) {
        clientHandlerList.remove(ch);
    }

    public void updateClient(Client c){
        c.setBalance(c.getBalance()+10);
        tbsr.updateClient(c);
        clientList = tbsr.getClientList();
    }

    private boolean areListNotEquals() {

        if((clientList.size() != clientListSave.size()))return true;

        for(int i = 0 ; i < clientListSave.size() ; i++){
            if(!clientList.get(i).hasSameProperty(clientListSave.get(i)))return true;
        }

        return false;
    }


    @Override
    public void run() {
        while (true) { // en écoute des maj de la table du joueur
            try {
                /// ACTION sur la table

                // Envoi de la table si elle a été modifiée (check des listes)
                if (areListNotEquals()) {
                    logger.info("Modification détectée. Ancienne liste : " + clientListSave);
                    logger.info("Nouvelle liste : " + clientList);

                    clientListSave.clear();
                    clientListSave.addAll(clientList);

                    writeObject();

                    logger.info("clientListSave mise à jour : " + clientListSave);
                }

                Thread.sleep(1000);

            } catch (Exception ignored) {
            }
        }
    }

    private synchronized void writeObject() throws IOException {
        for (ClientHandler clientHandler : clientHandlerList) {
            writerObject = new ObjectOutputStream(clientHandler.getClientSocket().getOutputStream());
            writerObject.writeObject(tbsr);
            writerObject.flush();
            logger.info("Table " + tbsr + " envoyée à " + clientHandler.getClient().getPseudo());
        }
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