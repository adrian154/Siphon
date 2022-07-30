package dev.bithole.siphon.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SiphonConfig {

    private static final File CLIENTS_FILE = new File("siphon.json");

    private final Gson gson;
    private final Map<String, Client> clients;
    private int port;
    private boolean allowCrossOrigin;

    public SiphonConfig() throws IOException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.clients = new HashMap<>();
        this.port = 20560;
        this.allowCrossOrigin = false;
        this.load();
    }

    public void load() throws IOException {
        clients.clear();
        if(CLIENTS_FILE.exists()) {

            Config config = gson.fromJson(Files.readString(CLIENTS_FILE.toPath()), Config.class);

            // build clients map
            for(Client client: config.clients) {
                client.revive();
                addClient(client);
            }

            this.port = config.port;
            this.allowCrossOrigin = config.allowCrossOrigin;

        } else {
            save();
        }
    }

    public void save() throws IOException {
        Files.writeString(CLIENTS_FILE.toPath(), gson.toJson(new Config(clients.values(), port, allowCrossOrigin)), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void addClient(Client client) {
        if(clients.containsKey(client.name)) {
            throw new IllegalArgumentException(String.format("Duplicate client named \"%s\"", client.name));
        }
        clients.put(client.name, client);
    }

    public int getPort() {
        return port;
    }

    public void removeClient(Client client) {
        clients.remove(client.name);
    }

    public Client getClient(String name) {
        return clients.get(name);
    }

    public Collection<Client> getClients() {
        return clients.values();
    }

    public boolean allowCrossOrigin() {
        return allowCrossOrigin;
    }

    // Gson doesn't support records :(
    private static class Config {

        public final Collection<Client> clients;
        public final int port;
        public final boolean allowCrossOrigin;

        public Config(Collection<Client> clients, int port, boolean allowCrossOrigin) {
            this.clients = clients;
            this.port = port;
            this.allowCrossOrigin = allowCrossOrigin;
        }

    }

}
