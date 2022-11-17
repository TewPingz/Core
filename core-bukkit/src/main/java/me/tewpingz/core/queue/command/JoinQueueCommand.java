package me.tewpingz.core.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.queue.Queue;
import me.tewpingz.core.server.Server;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("joinqueue")
@CommandPermission("core.joinqueue")
public class JoinQueueCommand extends BaseCommand {
    @Default
    @Syntax("<server>")
    @CommandCompletion("@servers")
    public void onCommand(Player player, Server.ServerSnapshot server) {
        Optional<Queue.QueueSnapshot> optional = CorePlugin.getInstance().getCore().getQueueManager().getQueueByPlayer(player.getUniqueId());

        if (optional.isPresent()) {
            Core.getInstance().getConfig().getErrorPalette().toBuilder()
                    .primary("You cannot join a queue as you are currently in a queue for").space()
                    .secondary(optional.get().getServerId()).tertiary(".")
                    .build(player::sendMessage);
            return;
        }

        Profile.ProfileSnapshot profile = CorePlugin.getInstance().getCore().getProfileManager().getCachedProfile(player.getUniqueId());
        CorePlugin.getInstance().getCore().getQueueManager().updateQueueAsync(server.getServerId(), queue -> queue.addPlayer(profile)).thenAccept(queue -> {
            int position = queue.getPosition(player.getUniqueId());
            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .primary("You have joined the queue for").space()
                    .secondary(server.getServerId()).space()
                    .primary("in position").space()
                    .tertiary("#").secondary(position + 1).tertiary(".")
                    .build(player::sendMessage);
        });
    }
}
