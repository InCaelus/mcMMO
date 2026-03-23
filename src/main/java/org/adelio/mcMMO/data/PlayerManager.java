package org.adelio.mcMMO.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerManager {
    private final MySQLManager mysql;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public PlayerManager(MySQLManager mysql) {
        this.mysql = mysql;
    }

    public CompletableFuture<PlayerData> loadPlayerData(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData data = mysql.loadData(uuid);
            cache.put(uuid, data);
            return data;
        });
    }

    public void savePlayerData(PlayerData data) {
        if (data != null) mysql.saveData(data);
    }

    public void removePlayer(UUID uuid) {
        cache.remove(uuid);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return cache.get(uuid);
    }

    public void saveAll() {
        cache.values().forEach(this::savePlayerData);
    }
}