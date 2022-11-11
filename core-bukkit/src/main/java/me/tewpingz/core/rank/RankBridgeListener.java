package me.tewpingz.core.rank;

import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.event.RankCreateEvent;
import me.tewpingz.core.rank.event.RankUpdateEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankBridgeListener {

    public RankBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(RankCreateEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutedBy()).space()
                    .primary("has created a rank named").space()
                    .append(event.getRankSnapshot().getColor().apply(Component.text(event.getRankSnapshot().getDisplayName())))
                    .tertiary(".")
                    .toString(this::broadcast);
        });

        instance.getCore().getBridge().registerListener(RankUpdateEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutedBy()).space()
                    .primary("has updated the rank named").space()
                    .append(event.getRankSnapshot().getColor().apply(Component.text(event.getRankSnapshot().getDisplayName())))
                    .tertiary(".")
                    .toString(this::broadcast);
        });
    }

    private void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("core.rank.alert") || player.isOp()) {
                player.sendMessage(message);
            }
        }
    }
}
