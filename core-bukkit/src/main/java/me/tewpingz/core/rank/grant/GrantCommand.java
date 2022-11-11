package me.tewpingz.core.rank.grant;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.core.util.uuid.UuidManager;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
        asyncUuid.fetchUuidAsync().thenAccept(uuid -> {
            if (uuid == null) {
                MessageBuilderDefaults.error()
                        .secondary(asyncUuid.getName())
                        .primary("has not joined the server before!")
                        .build(player::sendMessage);
                return;
            }

            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
                new GrantProcedure(uuid).start(player);
            });
        });
    }

    @Subcommand("add")
    @CommandPermission("core.grant.add")
    @Syntax("<target> <rank> <duration> <reason>")
    @CommandCompletion("@players @ranks perm")
    public void addGrant(CommandSender sender, AsyncUuid asyncUuid, Rank.RankSnapshot rankSnapshot, Duration duration, String reason) {
        asyncUuid.fetchUuidAsync().thenAccept(uuid -> {
            if (uuid == null) {
                MessageBuilderDefaults.error()
                        .secondary(asyncUuid.getName())
                        .primary("has not joined the server before!")
                        .build(sender::sendMessage);
                return;
            }

            Core.getInstance().getProfileManager().updateRealValueAsync(uuid, profile -> {
                profile.addGrant(rankSnapshot.getRankId(), sender.getName(), reason, duration.toMillis());
            });
        });
    }
}
