package me.tewpingz.core.util.uuid;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;

public class AsyncUuidContextResolver implements ContextResolver<AsyncUuid, BukkitCommandExecutionContext> {
    @Override
    public AsyncUuid getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        return new AsyncUuid(context.popFirstArg());
    }
}
