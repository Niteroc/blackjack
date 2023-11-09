package table;

import client.Client;
import server.ClientHandler;
import server.TableHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class TableSR implements Serializable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static final long serialVersionUID = 1L;

    private List<Client> clientList = new ArrayList<>();

    public TableSR() {}

    public int getTableHandlerId() {
        return TableHandler.getId();
    }

    @Override
    public String toString() {
        return "TableSR{" + "clientList=" + clientList + '}';
    }

    public synchronized void updateClient(Client c){
        logger.info(c.getPseudo() + " inséré/maj dans la table " + TableHandler.getId() + " // déjà existant : " + clientList.contains(c));
        if(!clientList.contains(c)){
            clientList.add(c);
        }else{
            clientList.set(clientList.indexOf(c),c);
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
