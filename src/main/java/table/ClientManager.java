package table;

import client.Client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private static List<Client> clientList = new ArrayList<>();

    public static List<Client> getClientList() {
        return clientList;
    }

    public static void addClient(Client client) {
        clientList.add(client);
    }

    public static void removeClient(Client client) {
        clientList.remove(client);
    }
}