package me.tewpingz.core.profile.grant;

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
    private final Table<UUID, Grant, BukkitTask> tasks = HashBasedTable.create();
    private final Lock lock = new ReentrantLock();

    public void schedule(UUID playerId, Grant grant) {
        if (grant.isInfinite() || grant.hasExpired()) {
            return;
        }

        this.lock.lock();
        int ticks = (int) (grant.getTimeLeft() / 1000 * 20);
        Runnable runnable = () -> Core.getInstance().getProfileManager().updateRealValueAsync(playerId, profile -> profile.removeGrant(grant, "CONSOLE", "Expired"));
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), runnable, ticks);
        this.tasks.put(playerId, grant, task);
        this.lock.unlock();
    }

    public void unschedule(UUID playerId, Grant.ExpiredGrant expiredGrant) {
        if (expiredGrant.getGrant().isInfinite()) {
            return;
        }

        this.lock.lock();
        BukkitTask task = this.tasks.remove(playerId, expiredGrant.getGrant());
        if (task != null) {
            task.cancel();
        }
        this.lock.unlock();
    }

    public void terminate(UUID playerId) {
        this.lock.lock();
        Map<Grant, BukkitTask> grantMap = this.tasks.rowMap().remove(playerId);
        if (grantMap != null) {
            grantMap.values().forEach(BukkitTask::cancel);
        }
        this.lock.unlock();
    }
}
