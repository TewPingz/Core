package me.tewpingz.core.rank.grant;

import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.grant.event.GrantCreateEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GrantBridgeListener {
    public GrantBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(GrantCreateEvent.class, (charSequence, grantCreateEvent) -> {
            if (Bukkit.getPlayer(grantCreateEvent.getPlayerUuid()) != null) {
                instance.getGrantScheduleManager().scheduleTask(grantCreateEvent.getPlayerUuid(), grantCreateEvent.getGrant());

                MessageBuilderDefaults.normal()
                        .tertiary("[Server Monitor]")
                        .secondary(grantCreateEvent.getExecutorName())
                        .primary("has granted")
                        .secondary(grantCreateEvent.getGrant().getRankSnapshot().getDisplayName())
                        .primary("to")
                        .secondary(grantCreateEvent.getPlayerUuid().toString())
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
