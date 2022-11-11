package me.tewpingz.core.rank.grant;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GrantScheduleManager {
    private final Table<UUID, Grant, BukkitTask> grantTasks = HashBasedTable.create();
    private final Lock grantLock = new ReentrantLock();

    protected void scheduleTask(UUID playerId, Grant grant) {
        if (grant.isInfinite() || grant.hasExpired()) {
            return;
        }

        this.grantLock.lock();
        int ticks = (int) (grant.getTimeLeft() / 1000 * 20);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(CorePlugin.getInstance(),
                () -> Core.getInstance().getProfileManager().updateRealValueAsync(playerId, profile -> profile.removeGrant(grant, "CONSOLE", "Expired")), ticks);
        this.grantTasks.put(playerId, grant, task);
        this.grantLock.unlock();
    }

    protected void unscheduledTasks(UUID playerId) {
        this.grantLock.lock();
        Map<Grant, BukkitTask> grantMap = this.grantTasks.rowMap().remove(playerId);
        if (grantMap != null) {
            grantMap.values().forEach(BukkitTask::cancel);
        }
        this.grantLock.unlock();
    }
}
