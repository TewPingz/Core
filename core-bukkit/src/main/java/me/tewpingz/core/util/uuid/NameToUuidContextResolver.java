package me.tewpingz.core.util.uuid;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.tewpingz.core.Core;

public class NameToUuidContextResolver implements ContextResolver<UuidManager.NameToUuidEntry.NameToUuidSnapshot, BukkitCommandExecutionContext> {
    @Override
    public UuidManager.NameToUuidEntry.NameToUuidSnapshot getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        UuidManager.NameToUuidEntry.NameToUuidSnapshot snapshot = Core.getInstance().getUuidManager().getCachedUuid(context.popFirstArg());

        if (snapshot == null) {
            throw new InvalidCommandArgument("That player has not joined the server before.");
        }

        return snapshot;
    }
}
