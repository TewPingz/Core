package me.tewpingz.core.rank;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import me.tewpingz.core.CorePlugin;

public class RankContextResolver implements ContextResolver<Rank.RankSnapshot, BukkitCommandExecutionContext> {
    @Override
    public Rank.RankSnapshot getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String rankName = context.popFirstArg();
        Rank.RankSnapshot rank = CorePlugin.getInstance().getCore().getRankManager().getCachedRank(rankName);

        if (rank == null) {
            throw new InvalidCommandArgument("That rank does not exist");
        }

        return rank;
    }
}
