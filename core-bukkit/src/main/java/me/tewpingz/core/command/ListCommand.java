package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("list|players")
public class ListCommand extends BaseCommand {

    @Default
    public void onCommand(CommandSender sender) {
        Core.getInstance().getRankManager().getSortedRanksAsync().thenApply(ranks -> {
            StringBuilder stringBuilder = new StringBuilder();
            ranks.forEach(rank -> {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(ChatColor.WHITE).append(", ");
                }
                stringBuilder.append(rank.getColor()).append(rank.getDisplayName());
            });
            return stringBuilder.toString();
        }).thenAccept(rankList -> {
            int amount = Core.getInstance().getProfileManager().getCachedValues().size();
            StringBuilder message = new StringBuilder(ChatColor.GRAY.toString());
            message.append("(").append(amount).append("/").append(Bukkit.getMaxPlayers()).append("): ");
            Core.getInstance().getProfileManager().forEachCachedValue(profile -> message.append(profile.getDisplayRank().getColor()).append(profile.getLastSeenName()));
            sender.sendMessage("");
            sender.sendMessage(rankList);
            sender.sendMessage(message.toString());
            sender.sendMessage("");
        });
    }

}
