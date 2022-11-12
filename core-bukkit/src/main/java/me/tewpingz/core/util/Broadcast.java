package me.tewpingz.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Broadcast {
    public static void broadcast(String message, String permission) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission) || player.isOp()) {
                player.sendMessage(message);
            }
        }
    }
}
