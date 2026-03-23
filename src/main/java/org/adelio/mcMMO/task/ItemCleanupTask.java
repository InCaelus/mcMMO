package org.adelio.mcMMO.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemCleanupTask extends BukkitRunnable {

    @Override
    public void run() {
        int count = 0;

        for (World world : Bukkit.getWorlds()) {

            for (Item item : world.getEntitiesByClass(Item.class)) {
                if (item.getTicksLived() >= 2400) {
                    item.remove();
                    count++;
                }
            }
        }

        if (count > 0) {
            Bukkit.getLogger().info("[mcMMO] 청소 완료: 방치된 아이템 " + count + "개를 삭제했습니다.");
        }
    }
}