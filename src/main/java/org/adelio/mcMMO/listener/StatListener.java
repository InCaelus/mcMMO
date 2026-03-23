package org.adelio.mcMMO.listener;

import net.kyori.adventure.text.Component;
import org.adelio.mcMMO.data.DataManager;
import org.adelio.mcMMO.data.PlayerData;
import org.adelio.mcMMO.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StatListener implements Listener {

    private final DataManager dataManager;
    private static final String GUI_TITLE = "§8[ mcMMO 스탯 정보 ]";

    public StatListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, Component.text(GUI_TITLE));
        PlayerData data = dataManager.getPlayerManager().getPlayerData(player.getUniqueId());

        if (data == null) return;

        gui.setItem(1, createSkillItem(Material.DIAMOND_PICKAXE, SkillType.MINING, data, "§b3x3 채광 & 추가 드랍"));
        gui.setItem(3, createSkillItem(Material.DIAMOND_AXE, SkillType.WOODCUTTING, data, "§6트리 펠러 (웅크리기)"));
        gui.setItem(5, createSkillItem(Material.DIAMOND_SHOVEL, SkillType.EXCAVATION, data, "§e보물 발견 확률 증가"));
        gui.setItem(7, createSkillItem(Material.DIAMOND_SWORD, SkillType.COMBAT, data, "§c레벨당 데미지 & 크리티컬"));

        player.openInventory(gui);
    }

    private ItemStack createSkillItem(Material m, SkillType type, PlayerData data, String abilityDesc) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.displayName(Component.text("§e§l" + type.getName()));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§f레벨: §a" + data.getLevel(type)));
        lore.add(Component.text("§f경험치: §7" + String.format("%.1f", data.getExp(type)) + " / " + (data.getLevel(type) * 100)));
        lore.add(Component.text(""));
        lore.add(Component.text("§7[ 고유 능력 ]"));
        lore.add(Component.text(abilityDesc));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().toString().contains(GUI_TITLE) ||
                event.getView().getTitle().equals(GUI_TITLE)) {

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }
}