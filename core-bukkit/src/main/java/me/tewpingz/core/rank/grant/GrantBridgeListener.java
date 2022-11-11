package me.tewpingz.core.rank.grant;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.rank.grant.event.GrantCreateEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GrantBridgeListener {
    public GrantBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(GrantCreateEvent.class, (charSequence, event) -> {
            if (Bukkit.getPlayer(event.getPlayerUuid()) != null) {
                instance.getGrantScheduleManager().scheduleTask(event.getPlayerUuid(), event.getGrant());
                MessageBuilderDefaults.normal()
                        .tertiary("[Server Monitor]").space()
                        .secondary(event.getExecutorName()).space()
                        .primary("has granted").space()
                        .append(event.getGrant().getRankSnapshot().getColor().apply(Component.text(event.getGrant().getRankSnapshot().getDisplayName()))).space()
                        .primary("to").space()
                        .secondary(Core.getInstance().getUuidManager().getName(event.getPlayerUuid()).getName())
                        .toString(this::broadcast);
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
