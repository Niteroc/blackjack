package server;

import client.Client;
import table.TableSR;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TableHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private final int id;

    private static int number = 0;

    private static ObjectOutputStream writerObject;

    private final static List<ClientHandler> clientHandlerList = new ArrayList<>();

    private final TableSR tbsr = new TableSR(this);


    public TableHandler() throws IOException {
        id = ++number;
    }

    public void addClientHandler(ClientHandler ch) {
        clientHandlerList.add(ch);
    }

    public void removeClientHandler(ClientHandler ch) {
        clientHandlerList.remove(ch);
    }

    public void updateClient(Client c){
        tbsr.updateClient(c);
    }

    @Override
    public void run() {
        while (true) { // en écoute des maj de la table du joueur
            try {

                /// ACTION sur la table
                writeObject(tbsr);

            } catch (Exception ignored) {
            }
        }
    }

    private void writeObject(TableSR tbsr) throws IOException {
        for (ClientHandler clientHandler : clientHandlerList) {
            writerObject = new ObjectOutputStream(clientHandler.getClientSocket().getOutputStream());
            writerObject.writeObject(tbsr);
            writerObject.flush();
        }
    }

    public int getId() {
        return id;
    }
}
