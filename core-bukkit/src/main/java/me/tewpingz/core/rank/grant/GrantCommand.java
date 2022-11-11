package me.tewpingz.core.rank.grant;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.rank.Rank;
import me.tewpingz.core.util.uuid.UuidManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@CommandAlias("rankgrant|grant")
public class GrantCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players @ranks")
    @Syntax("<target> <rank> <reason>")
    public void onCommand(Player player, UuidManager.NameToUuidEntry.NameToUuidSnapshot uuidSnapshot, Rank.RankSnapshot rankSnapshot, Duration duration, String reason) {
        Core.getInstance().getProfileManager().updateRealValueAsync(uuidSnapshot.getUuid(), profile -> {
            profile.addGrant(rankSnapshot.getRankId(), player.getName(), reason, duration.toMillis());
        });
    }

    @Subcommand("add")
    @CommandPermission("core.grant.add")
    @Syntax("<target> <rankName> <reason>")
    public void addGrant(CommandSender sender, Player player, String rankName, String reason) {
        Core.getInstance().getProfileManager().updateRealValueAsync(player.getUniqueId(), profile -> {
            profile.addGrant(rankName, sender.getName(), reason, TimeUnit.SECONDS.toMillis(60));
        });
    }
}
