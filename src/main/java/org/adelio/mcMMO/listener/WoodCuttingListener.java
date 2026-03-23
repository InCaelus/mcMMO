package org.adelio.mcMMO.listener;

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
import java.util.*;

public class WoodCuttingListener implements Listener {
    private final PlayerManager playerManager;
    public WoodCuttingListener(PlayerManager pm) { this.playerManager = pm; }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        String toolName = p.getInventory().getItemInMainHand().getType().name();

        if (toolName.contains("PICKAXE") || !toolName.endsWith("_AXE")) return;

        Block block = event.getBlock();
        if (!isLog(block.getType())) return;

        PlayerData data = playerManager.getPlayerData(p.getUniqueId());
        if (data == null) return;

        int level = data.getLevel(SkillType.WOODCUTTING);

        if (level >= 30 && p.isSneaking()) {
            breakTree(block, p, level * 20);
        }

        double xp = 1.5;
        data.addXp(SkillType.WOODCUTTING, xp);
        sendActionBar(p, SkillType.WOODCUTTING, xp, data);
    }

    private void breakTree(Block start, Player p, int limit) {
        Queue<Block> queue = new LinkedList<>();
        Set<Block> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        int count = 0;
        while (!queue.isEmpty() && count < limit) {
            Block curr = queue.poll();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Block rel = curr.getRelative(x, y, z);
                        if (!visited.contains(rel) && isLog(rel.getType())) {
                            visited.add(rel);
                            queue.add(rel);
                            rel.breakNaturally(p.getInventory().getItemInMainHand());
                            count++;
                        }
                    }
                }
            }
        }
    }

    private boolean isLog(Material m) { return m.name().contains("LOG") || m.name().contains("WOOD"); }
    private void sendActionBar(Player p, SkillType t, double g, PlayerData d) {
        String msg = String.format("§a[%s] §f+%.1f XP §7(%.1f/%.1f)", t.getName(), g, d.getExp(t), d.getLevel(t)*100.0);
        p.sendActionBar(LegacyComponentSerializer.legacySection().deserialize(msg));
    }
}