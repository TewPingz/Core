package me.tewpingz.core.rank;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import me.tewpingz.core.CorePlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RankCommandCompletion implements CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<String> completion = new ArrayList<>();
        CorePlugin.getInstance().getCore().getRankManager().getCachedRanks().forEach(rank -> completion.add(rank.getRankId()));
        return completion;
    }
}
