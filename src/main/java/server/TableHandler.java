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

    private static List<ClientHandler> clientHandlerList = new ArrayList<>();

    private final TableSR tbsr = new TableSR();

    private TableHandler tableHandlerSave = null;


    public TableHandler() throws IOException {
    }

    public synchronized void addClientHandler(ClientHandler ch) {
        clientHandlerList.add(ch);
    }

    public synchronized void removeClientHandler(ClientHandler ch) {
        clientHandlerList.remove(ch);
    }

    public void updateClient(Client c){
        tbsr.updateClient(c);
    }

    @Override
    public void run() {

        while (true) { // en écoute des maj de la table du joueur
            try {
                tableHandlerSave = this;

                /// ACTION sur la table

                // Envoi de la table si elle a été modifiée
                if(!tableHandlerSave.equals(this))writeObject();

            } catch (Exception ignored) {
            }
        }
    }

    public synchronized void writeFirstTable(ClientHandler clientHandler) throws IOException, InterruptedException {
        writerObject = new ObjectOutputStream(clientHandler.getClientSocket().getOutputStream());
        writerObject.writeObject(tbsr);
        writerObject.flush();
        logger.info("Table " + tbsr + " envoyée");
    }

    private synchronized void writeObject() throws IOException, InterruptedException {
        for (ClientHandler clientHandler : clientHandlerList) {
            writerObject = new ObjectOutputStream(clientHandler.getClientSocket().getOutputStream());
            writerObject.writeObject(tbsr);
            writerObject.flush();
        }
    }

    public static int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableHandler that = (TableHandler) o;
        return Objects.equals(tbsr, that.tbsr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tbsr);
    }

    @Override
    public String toString() {
        return "TableHandler - id:" +id + "{" +
                "tbsr=" + tbsr +
                '}';
    }
}
