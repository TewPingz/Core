package me.tewpingz.core.server.command;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.tewpingz.core.Core;
import me.tewpingz.core.server.Server;

public class ServerContextResolver implements ContextResolver<Server.ServerSnapshot, BukkitCommandExecutionContext> {
    @Override
    public Server.ServerSnapshot getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String serverId = context.popFirstArg();
        Server.ServerSnapshot snapshot = Core.getInstance().getServerManager().getCachedServer(serverId);

        if (snapshot == null) {
            throw new InvalidCommandArgument("The server you have provided is invalid.");
        }

        return snapshot;
    }
}
