package org.adelio.mcMMO.command;

import org.adelio.mcMMO.data.DataManager;
import org.adelio.mcMMO.data.PlayerData;
import org.adelio.mcMMO.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminCommand implements CommandExecutor {
    private final DataManager dataManager;

    public AdminCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                return true;
            }

            PlayerData data = dataManager.getPlayerManager().getPlayerData(target.getUniqueId());
            data.resetAll();
            dataManager.getMysqlManager().saveData(data); // DB 저장

            target.sendMessage("§e[mcMMO] §f관리자에 의해 모든 스탯이 §c초기화§f되었습니다.");
            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 0.5f);

            sender.sendMessage("§a" + target.getName() + "님의 모든 스탯을 초기화했습니다.");
            return true;
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c플레이어를 찾을 수 없습니다.");
                return true;
            }

            PlayerData data = dataManager.getPlayerManager().getPlayerData(target.getUniqueId());
            String skillInput = args[2].toUpperCase();

            try {
                int newLevel = Integer.parseInt(args[3]);
                SkillType type = SkillType.valueOf(skillInput);

                data.setLevel(type, newLevel);
                data.setExp(type, 0.0);
                dataManager.getMysqlManager().saveData(data);

                target.sendMessage("§e[mcMMO] §f관리자에 의해 §b" + type.getName() +
                        " §f레벨이 §a" + newLevel + "§f(으)로 조정되었습니다.");
                target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

                sender.sendMessage("§e[mcMMO] §f" + target.getName() + "님의 §b" + type.getName() +
                        " §f레벨을 §a" + newLevel + "§f(으)로 설정했습니다.");
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c존재하지 않는 스킬입니다. (MINING, WOODCUTTING, EXCAVATION, COMBAT)");
            } catch (Exception e) {
                sender.sendMessage("§c올바른 숫자를 입력해주세요.");
            }
            return true;
        }

        sender.sendMessage("§7사용법: /mcmmo set <플레이어> <스킬> <레벨>");
        sender.sendMessage("§7사용법: /mcmmo reset <플레이어>");
        return true;
    }
}