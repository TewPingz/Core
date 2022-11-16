package me.tewpingz.core.util.uuid;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import me.tewpingz.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AsyncUuidCommandCompletion implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        Set<String> completion = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            completion.add(player.getName());
        }

        Core.getInstance().getServerManager().getCachedServers().forEach(server -> {
            server.getPlayers().forEach(player -> completion.add(player.getUsername()));
        });

        return completion;
    }
}
