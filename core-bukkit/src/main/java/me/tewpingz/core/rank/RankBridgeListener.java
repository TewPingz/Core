package me.tewpingz.core.rank;

import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.event.RankCreateEvent;
import me.tewpingz.core.rank.event.RankUpdateEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankBridgeListener {

    public RankBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(RankCreateEvent.class, (charSequence, rankCreateEvent) -> {
            this.broadcast(MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]")
                    .secondary(rankCreateEvent.getExecutedBy())
                    .primary("has created a rank named")
                    .secondary(rankCreateEvent.getRankName())
                    .build());
        });

        instance.getCore().getBridge().registerListener(RankUpdateEvent.class, (charSequence, rankUpdateEvent) -> {
            this.broadcast(MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]")
                    .secondary(rankUpdateEvent.getExecutedBy())
                    .primary("has updated the rank named")
                    .secondary(rankUpdateEvent.getRankName())
                    .build());
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
