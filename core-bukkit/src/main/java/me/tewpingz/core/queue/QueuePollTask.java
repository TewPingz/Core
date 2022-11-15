package me.tewpingz.core.queue;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.queue.event.QueuePollEvent;
import org.bukkit.Bukkit;

public class QueuePollTask implements Runnable {
    @Override
    public void run() {
        // Don't poll the server if it has a whitelist
        // Staff should be skipped from the queue when hub core is done
        if (Bukkit.hasWhitelist()) {
            return;
        }

        // Don't poll if server is full to prevent people from getting kicked
        // from queue simply because they cant log onto the server.
        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
            return;
        }

        String serverId = CorePlugin.getInstance().getServerInitializer().getConfig().getServerId();
        Core.getInstance().getBridge().callEvent(new QueuePollEvent(serverId));
    }
}
