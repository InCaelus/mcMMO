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
import org.bukkit.inventory.ItemStack;

public class MiningListener implements Listener {
    private final PlayerManager playerManager;
    public MiningListener(PlayerManager pm) { this.playerManager = pm; }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Material tool = p.getInventory().getItemInMainHand().getType();

        if (!tool.name().endsWith("_PICKAXE")) return;

        Block block = event.getBlock();
        Material mat = block.getType();
        if (!isMineable(mat)) return;

        PlayerData data = playerManager.getPlayerData(p.getUniqueId());
        if (data == null) return;

        int level = data.getLevel(SkillType.MINING);

        double xp = switch (mat) {
            case ANCIENT_DEBRIS -> 50.0;
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE, EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> 20.0;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> 8.0;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> 5.0;
            case COAL_ORE, DEEPSLATE_COAL_ORE, LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> 2.0;
            case NETHER_QUARTZ_ORE -> 1.5;
            case STONE, DEEPSLATE, TUFF, NETHERRACK -> 0.1;
            default -> 0.05;
        };

        data.addXp(SkillType.MINING, xp);
        sendActionBar(p, SkillType.MINING, xp, data);

        if (level >= 30) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        Block rel = block.getRelative(x, y, z);
                        if (isMineable(rel.getType())) rel.breakNaturally(p.getInventory().getItemInMainHand());
                    }
                }
            }
        }

        if (Math.random() < (level * 0.01)) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(mat));
        }
    }

    private boolean isMineable(Material m) {
        String n = m.name();
        return n.contains("ORE") || n.contains("STONE") || n.contains("DEEPSLATE") || n.equals("NETHERRACK") || n.equals("TUFF");
    }

    private void sendActionBar(Player p, SkillType t, double g, PlayerData d) {
        String msg = String.format("§b[%s] §f+%.1f XP §7(%.1f/%.1f)", t.getName(), g, d.getExp(t), d.getLevel(t)*100.0);
        p.sendActionBar(LegacyComponentSerializer.legacySection().deserialize(msg));
    }
}