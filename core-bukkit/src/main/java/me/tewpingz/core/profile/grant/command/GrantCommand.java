package me.tewpingz.core.profile.grant.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.grant.GrantProcedure;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@CommandAlias("grant")
@CommandPermission("core.grant")
public class GrantCommand extends BaseCommand {

    @Default
    @Syntax("<target>")
    @CommandPermission("core.grant.add")
    @CommandCompletion("@players")
    public void onCommand(Player player, AsyncUuid asyncUuid) {
        asyncUuid.fetchUuid(player, uuid -> {
            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                new GrantProcedure(uuid).start(player);
            });
        });
    }

    @Subcommand("add")
    @CommandPermission("core.grant.add")
    @Syntax("<target> <rank> <duration> <reason>")
    @CommandCompletion("@players @ranks @duration Staff|Donor")
    public void addGrant(CommandSender sender, AsyncUuid asyncUuid, Rank.RankSnapshot rankSnapshot, Duration duration, String reason) {
        asyncUuid.fetchUuid(sender, uuid -> {
            Core.getInstance().getProfileManager().updateRealValueAsync(uuid, profile -> {
                profile.addGrant(rankSnapshot.getRankId(), sender.getName(), reason, duration.toMillis());
            });
        });
    }
}
