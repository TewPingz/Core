package me.tewpingz.core.profile.grant.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.grant.GrantProcedure;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@CommandAlias("grant")
@CommandPermission("core.grant")
public class GrantCommand extends BaseCommand {
    @Default
    @Syntax("<target>")
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
    @CommandCompletion("@players @ranks @duration @empty")
    public void addGrant(CommandSender sender, AsyncUuid asyncUuid, Rank.RankSnapshot rankSnapshot, Duration duration, String reason) {
        if (rankSnapshot.getRankId().equalsIgnoreCase("default")) {
            MessageBuilderDefaults.error().primary("You cannot grant the").space()
                    .append(rankSnapshot.getDisplayNameWithColor()).space().primary("rank").tertiary("!")
                    .build(sender::sendMessage);
            return;
        }

        asyncUuid.fetchUuid(sender, uuid -> {
            Core.getInstance().getProfileManager().updateRealProfileAsync(uuid, profile -> {
                profile.addGrant(rankSnapshot, sender.getName(), reason, duration.toMillis());
            });
        });
    }
}
