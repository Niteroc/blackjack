package table;

import client.Client;
import server.ClientHandler;
import server.TableHandler;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

public class TableSR implements Serializable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static final long serialVersionUID = 1L;

    private HashMap<Client, HandSR> handSRClientHashMap = new HashMap<Client, HandSR>(); // relation entre un Client et sa Main

    public List<Client> getClientList() {
        return clientList;
    }

    public HandSR retrievePlayerHand(Client client){
        return handSRClientHashMap.get(client);
    }

    public Client findClientByHand(HandSR handSR){
        for (Map.Entry<Client, HandSR> entry : handSRClientHashMap.entrySet()) {
            if (entry.getValue().equals(handSR)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Client getClientModification(Client client){
        for (Client client1 : clientList){
            if(client1.equals(client))return client1;
        }
        return client;
    }

    private List<Client> clientList = new ArrayList<>();

    public TableSR() {}

    public int getTableHandlerId() {
        return TableHandler.getId();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("TableSR{\n");

        for (Client client : clientList) {
            stringBuilder.append("\t").append(client).append('\n');
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }
    public void updateClient(Client c) {
        logger.info(c.getPseudo() + " inséré/maj dans la table " + TableHandler.getId() + " // déjà existant : " + clientList.contains(c));
        if(!clientList.contains(c)){
            clientList.add(c);
            handSRClientHashMap.put(c, new HandSR());
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
