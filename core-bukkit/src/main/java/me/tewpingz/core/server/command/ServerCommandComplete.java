package me.tewpingz.core.server.command;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import me.tewpingz.core.Core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerCommandComplete implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> serverIds = new ArrayList<>();
        Core.getInstance().getServerManager().getCachedValues().forEach(server -> serverIds.add(server.getServerId()));
        return serverIds;
    }
}
