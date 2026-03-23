package org.adelio.mcMMO.data;

import org.adelio.mcMMO.skill.SkillType;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    public static final int MAX_LEVEL = 30; // 스탯 최대치 설정
    private final UUID uuid;
    private final Map<SkillType, Integer> levels = new HashMap<>();
    private final Map<SkillType, Double> exp = new HashMap<>();
    private int statPoints = 0;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        for (SkillType type : SkillType.values()) {
            levels.put(type, 1);
            exp.put(type, 0.0);
        }
    }
    public void addXp(SkillType type, double amount) {
        if (getLevel(type) >= MAX_LEVEL) return;

        double currentExp = getExp(type) + amount;
        int currentLevel = getLevel(type);
        double nextLevelExp = currentLevel * 100.0;

        if (currentExp >= nextLevelExp) {
            currentExp -= nextLevelExp;

            int nextLevel = currentLevel + 1;
            setLevel(type, nextLevel);
            addStatPoints(1);

            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
                p.sendTitle("§e§lLEVEL UP!", "§f" + type.getName() + " 레벨이 §a" + nextLevel + "§f이 되었습니다.", 10, 40, 10);
                if (nextLevel == MAX_LEVEL) {
                    celebrateMaxLevel(p, type);
                }
            }
        }
        setExp(type, currentExp);
    }

    private void celebrateMaxLevel(Player p, SkillType type) {
        String rawMsg = String.format("§b§l[mcMMO] §e§l%s§f님이 §6§l%s §f스탯을 §c§l최고 레벨(30)§f까지 마스터했습니다!",
                p.getName(), type.getName());
        net.kyori.adventure.text.Component announcement = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(rawMsg);
        Bukkit.broadcast(announcement);

        Location loc = p.getLocation();
        org.bukkit.plugin.Plugin plugin = Bukkit.getPluginManager().getPlugin("mcMMO");
        if (plugin == null) return;

        for (int i = 0; i < 3; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                spawnCelebrationFirework(loc);
            }, i * 20L);
        }
    }

    private void spawnCelebrationFirework(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.BLUE, Color.YELLOW, Color.WHITE) // 금색 계열
                .withFade(Color.ORANGE) // 사라질 때 오렌지색
                .with(FireworkEffect.Type.BALL_LARGE) // 큰 폭발
                .trail(true) // 잔상 효과
                .flicker(true) // 반짝임 효과
                .build();

        meta.addEffect(effect);
        meta.setPower(1); // 높이 설정
        fw.setFireworkMeta(meta);
    }
    public UUID getUuid() { return uuid; }
    public int getLevel(SkillType type) { return levels.getOrDefault(type, 1); }
    public void setLevel(SkillType type, int lvl) { levels.put(type, Math.min(lvl, MAX_LEVEL)); }
    public double getExp(SkillType type) { return exp.getOrDefault(type, 0.0); }
    public void setExp(SkillType type, double xp) { exp.put(type, xp); }
    public int getStatPoints() { return statPoints; }
    public void addStatPoints(int amount) { this.statPoints += amount; }
    public boolean spendStatPoint() {
        if (statPoints > 0) { statPoints--; return true; }
        return false;
    }

    public void resetAll() {
        for (SkillType type : SkillType.values()) {
            levels.put(type, 1);
            exp.put(type, 0.0);
        }
        this.statPoints = 0;
    }
}