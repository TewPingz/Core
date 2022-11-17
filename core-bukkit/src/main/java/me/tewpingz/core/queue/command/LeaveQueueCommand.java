package me.tewpingz.core.queue.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import me.tewpingz.core.queue.Queue;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandPermission("core.leavequeue")
@CommandAlias("leavequeue")
public class LeaveQueueCommand extends BaseCommand {
    @Default
    public void onCommand(Player player) {
        Optional<Queue.QueueSnapshot> optional = Core.getInstance().getQueueManager().getQueueByPlayer(player.getUniqueId());

        if (optional.isEmpty()) {
            Core.getInstance().getConfig().getErrorPallet().toBuilder()
                    .primary("You are currently not queued for anything")
                    .tertiary(".")
                    .build(player::sendMessage);
            return;
        }

        Queue.QueueSnapshot snapshot = optional.get();
        Core.getInstance().getQueueManager().updateQueueAsync(snapshot.getServerId(), queue -> queue.removePlayer(player.getUniqueId())).thenAccept(serverQueue -> {
            Core.getInstance().getConfig().getSuccessPallet().toBuilder()
                    .primary("You have successfully left the queue for").space()
                    .secondary(serverQueue.getServerId()).tertiary(".")
                    .build(player::sendMessage);
        });
    }
}
