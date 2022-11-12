package me.tewpingz.core.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Broadcast {
    public static void broadcast(Component message, String permission) {
        Bukkit.getConsoleSender().sendMessage(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission) || player.isOp()) {
                player.sendMessage(message);
            }
        }
    }

    public static void broadcast(String message, String permission) {
        Bukkit.getConsoleSender().sendMessage(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission) || player.isOp()) {
                player.sendMessage(message);
            }
        }
    }
}
