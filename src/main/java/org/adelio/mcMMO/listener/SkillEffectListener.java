package org.adelio.mcMMO.listener;

import org.adelio.mcMMO.data.DataManager;
import org.adelio.mcMMO.data.PlayerData;
import org.adelio.mcMMO.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack; // 누락된 임포트 추가

public class SkillEffectListener implements Listener {
    private final DataManager dataManager;

    public SkillEffectListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData data = dataManager.getPlayerManager().getPlayerData(player.getUniqueId());
        if (data == null) return;

        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        String toolName = tool.getType().name();

        // 채광 만렙(30) 효과: 3x3 파괴
        if (data.getLevel(SkillType.MINING) >= 30 && toolName.contains("PICKAXE")) {
            if (isMiningTarget(block.getType())) {
                break3x3(block, tool);
            }
        }

        // 벌목 만렙(30) 효과: 트리 펠러
        if (data.getLevel(SkillType.WOODCUTTING) >= 30 && toolName.contains("_AXE")) {
            if (block.getType().name().contains("LOG")) {
                breakTree(block, tool, 0);
            }
        }
    }

    private void break3x3(Block center, ItemStack tool) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block target = center.getRelative(x, y, z);
                    if (isMiningTarget(target.getType())) {
                        target.breakNaturally(tool);
                    }
                }
            }
        }
    }

    private void breakTree(Block block, ItemStack tool, int count) {
        if (count > 64) return;
        if (!block.getType().name().contains("LOG")) return;

        block.breakNaturally(tool);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    breakTree(block.getRelative(x, y, z), tool, count + 1);
                }
            }
        }
    }

    private boolean isMiningTarget(Material m) {
        String name = m.name();
        return name.contains("ORE") || name.equals("STONE") || name.equals("DEEPSLATE") || name.contains("DIRT");
    }
}