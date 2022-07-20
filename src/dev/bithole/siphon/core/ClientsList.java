package dev.bithole.siphon.core;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientsList {

    private static final Type TYPE = new TypeToken<List<Client>>(){}.getType();
    private static final File CLIENTS_FILE = new File("siphon/clients.json");
    private static final Path FILE_DIR = CLIENTS_FILE.toPath().getParent();

    private Gson gson;
    private Map<String, Client> clients;

    public ClientsList() throws IOException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.clients = new HashMap<>();
        this.load();
    }

    public void load() throws IOException {
        Files.createDirectories(FILE_DIR);
        if(CLIENTS_FILE.exists()) {
            List<Client> clientsList = gson.fromJson(Files.readString(CLIENTS_FILE.toPath()), TYPE);
            for(Client client: clientsList) {
                client.revive();
                addClient(client);
            }
        } else {
            save();
        }
    }

    public void save() throws IOException {
        Files.writeString(CLIENTS_FILE.toPath(), gson.toJson(clients.values()), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void addClient(Client client) {
        if(clients.containsKey(client.name)) {
            throw new IllegalArgumentException(String.format("Duplicate client named \"%s\"", client.name));
        }
        clients.put(client.name, client);
    }

    public void removeClient(Client client) {
        clients.remove(client.name);
    }

    public Client getClient(String name) {
        return clients.get(name);
    }

}
