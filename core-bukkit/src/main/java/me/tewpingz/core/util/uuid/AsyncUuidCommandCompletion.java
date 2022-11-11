package me.tewpingz.core.util.uuid;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AsyncUuidCommandCompletion implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completion = new ArrayList<>();

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            completion.add(offlinePlayer.getName());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            completion.add(player.getName());
        }

        return completion;
    }
}
