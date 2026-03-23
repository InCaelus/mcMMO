package org.adelio.mcMMO.listener;

import org.adelio.mcMMO.data.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    private final PlayerManager playerManager;

    public PlayerJoinQuitListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerManager.loadPlayerData(event.getPlayer().getUniqueId())
                .thenAccept(data -> event.getPlayer().sendMessage("§a데이터 로드 완료!"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerManager.savePlayerData(playerManager.getPlayerData(event.getPlayer().getUniqueId()));
        playerManager.removePlayer(event.getPlayer().getUniqueId());
    }
}