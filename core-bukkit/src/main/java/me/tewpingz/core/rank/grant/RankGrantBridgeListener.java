package me.tewpingz.core.rank.grant;

import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.grant.event.RankGrantCreateEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankGrantBridgeListener {
    public RankGrantBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(RankGrantCreateEvent.class, (charSequence, rankGrantCreateEvent) -> {
            if (Bukkit.getPlayer(rankGrantCreateEvent.getPlayerUuid()) != null) {
                instance.getRankGrantScheduleManager().scheduleTask(rankGrantCreateEvent.getPlayerUuid(), rankGrantCreateEvent.getGrant());

                MessageBuilderDefaults.normal()
                        .tertiary("[Server Monitor]")
                        .secondary(rankGrantCreateEvent.getExecutorName())
                        .primary("has granted")
                        .secondary(rankGrantCreateEvent.getGrant().getRankSnapshot().getDisplayName())
                        .primary("to")
                        .secondary(rankGrantCreateEvent.getPlayerUuid().toString())
                        .build(this::broadcast);
            }
        });
    }

    private void broadcast(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("core.grant.alert") || player.isOp()) {
                player.sendMessage(message);
            }
        }
    }
}
