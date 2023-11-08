package table;

import client.Client;
import server.ClientHandler;
import server.TableHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TableSR implements Serializable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static final long serialVersionUID = 1L;

    private List<Client> clientList = new ArrayList<>();

    private transient TableHandler tableHandler;

    public TableSR(TableHandler tableHandler) {
        this.tableHandler = tableHandler;
    }

    @Override
    public String toString() {
        return "TableSR{" + "clientList=" + clientList + '}';
    }

    public void updateClient(Client c){
        logger.info(c.getPseudo() + " inséré/maj dans la table " + tableHandler.getId() + " // déjà existant : " + clientList.contains(c));
        if(!clientList.contains(c)){
            clientList.add(c);
        }else{
            clientList.set(clientList.indexOf(c),c);
        }
    }
}
