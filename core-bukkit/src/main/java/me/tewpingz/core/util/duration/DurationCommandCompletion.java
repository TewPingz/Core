package me.tewpingz.core.util.duration;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DurationCommandCompletion implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completion = new ArrayList<>();
        completion.add("1d");
        completion.add("3d");
        completion.add("10d");
        completion.add("30d");
        completion.add("60d");
        completion.add("perm");
        return completion;
    }
}
