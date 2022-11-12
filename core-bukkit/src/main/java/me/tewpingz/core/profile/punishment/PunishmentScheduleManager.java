package me.tewpingz.core.profile.punishment;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.grant.Grant;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PunishmentScheduleManager {
    private final Table<UUID, Punishment, BukkitTask> tasks = HashBasedTable.create();
    private final Lock lock = new ReentrantLock();

    protected void schedule(UUID playerId, Punishment punishment) {
        if (punishment.isInfinite() || punishment.hasExpired()) {
            return;
        }

        this.lock.lock();
        int ticks = (int) (punishment.getTimeLeft() / 1000 * 20);
        Runnable runnable = () -> Core.getInstance().getProfileManager().updateRealValueAsync(playerId, profile -> profile.removePunishment(punishment, "CONSOLE", "Expired"));
        BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), runnable, ticks);
        this.tasks.put(playerId, punishment, task);
        this.lock.unlock();
    }

    protected void unschedule(UUID playerId, Punishment.ExpiredPunishment punishment) {
        if (punishment.getGrant().isInfinite()) {
            return;
        }

        this.lock.lock();
        BukkitTask task = this.tasks.remove(playerId, punishment.getGrant());
        if (task != null) {
            task.cancel();
        }
        this.lock.unlock();
    }

    protected void terminate(UUID playerId) {
        this.lock.lock();
        Map<Punishment, BukkitTask> punishmentMap = this.tasks.rowMap().remove(playerId);
        if (punishmentMap != null) {
            punishmentMap.values().forEach(BukkitTask::cancel);
        }
        this.lock.unlock();
    }
}
