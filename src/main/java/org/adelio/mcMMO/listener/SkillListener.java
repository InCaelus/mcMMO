package org.adelio.mcMMO.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.adelio.mcMMO.data.PlayerData;
import org.adelio.mcMMO.data.PlayerManager;
import org.adelio.mcMMO.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SkillListener implements Listener {
    private final PlayerManager playerManager;

    public SkillListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData data = playerManager.getPlayerData(player.getUniqueId());
        if (data == null) return;

        Block block = event.getBlock();
        Material material = block.getType();
        double xpGain = 0;
        String name = "";

        xpGain = switch (material) {
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> { name = "다이아몬드"; yield 100.0; }
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> { name = "에메랄드"; yield 120.0; }
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> { name = "금"; yield 35.0; }
            case IRON_ORE, DEEPSLATE_IRON_ORE -> { name = "철"; yield 15.0; }
            case COAL_ORE, DEEPSLATE_COAL_ORE -> { name = "석탄"; yield 5.0; }
            case ANCIENT_DEBRIS -> { name = "고대 잔해"; yield 300.0; }
            case STONE, DEEPSLATE, TUFF -> { name = "돌"; yield 1.0; }
            default -> 0.0;
        };

        if (xpGain > 0) {
            data.addXp(SkillType.MINING, xpGain);
            sendActionBar(player, SkillType.MINING, xpGain, name, data);
        }

        if (material.name().contains("LOG")) {
            xpGain = 15.0;
            data.addXp(SkillType.WOODCUTTING, xpGain);
            sendActionBar(player, SkillType.WOODCUTTING, xpGain, "나무", data);
        }
    }

    private void sendActionBar(Player player, SkillType type, double gain, String name, PlayerData data) {
        double currentXp = data.getExp(type);
        double nextXp = data.getLevel(type) * 100.0;

        String message = String.format("§6[%s] §b%s §f+%.1f XP §7(%.1f / %.1f)",
                type.getName(), name, gain, currentXp, nextXp);

        Component component = LegacyComponentSerializer.legacySection().deserialize(message);
        player.sendActionBar(component);
    }
}