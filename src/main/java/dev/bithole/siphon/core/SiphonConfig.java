package dev.bithole.siphon.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class SiphonConfig {

    private static final File CLIENTS_FILE = new File("siphon.json");

    private final Gson gson;
    private final Map<String, Client> clients;
    private int port;
    private Set<String> allowedOrigins;

    public SiphonConfig() throws IOException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.clients = new HashMap<>();
        this.port = 20560;
        this.allowedOrigins = Collections.emptySet();
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
            this.allowedOrigins = config.allowedOrigins;

        } else {
            save();
        }
    }

    public void save() throws IOException {
        Files.writeString(CLIENTS_FILE.toPath(), gson.toJson(new Config(clients.values(), port, allowedOrigins)), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

    public boolean originIsAllowed(String origin) {
        return allowedOrigins.contains(origin);
    }

    // Gson doesn't support records :(
    private static class Config {

        public final Collection<Client> clients;
        public final int port;
        public final Set<String> allowedOrigins;

        public Config(Collection<Client> clients, int port, Set<String> allowedOrigins) {
            this.clients = clients;
            this.port = port;
            this.allowedOrigins = allowedOrigins;
        }

    }

}
