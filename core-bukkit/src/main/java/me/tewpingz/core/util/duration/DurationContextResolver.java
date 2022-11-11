package me.tewpingz.core.util.duration;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.tewpingz.core.util.TimeUtil;

import java.time.Duration;

public class DurationContextResolver implements ContextResolver<Duration, BukkitCommandExecutionContext> {
    @Override
    public Duration getContext(BukkitCommandExecutionContext bukkitCommandCompletionContext) throws InvalidCommandArgument {
        String input = bukkitCommandCompletionContext.popFirstArg();

        if (input.equalsIgnoreCase("permanent") || input.equalsIgnoreCase("perm")) {
            return Duration.ofMillis(-1);
        }

        Long millis = TimeUtil.parseTime(input);
        if (millis == null) {
            throw new InvalidCommandArgument("That is an invalid duration: %s".formatted(input));
        }

        return Duration.ofMillis(millis);
    }
}
