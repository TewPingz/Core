package me.tewpingz.core.server.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.server.event.*;
import me.tewpingz.core.util.Broadcast;
import me.tewpingz.message.MessageBuilderDefaults;

public class ServerBridgeListener {

    public ServerBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(ServerOnlineEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getServer().getDisplayName()).space()
                    .primary("has just gone online").tertiary("!")
                    .build(component -> Broadcast.broadcast(component, "core.server.alert"));
        });

        instance.getCore().getBridge().registerListener(ServerShutdownEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getServer().getDisplayName()).space()
                    .primary("has just gone offline").tertiary("!")
                    .build(component -> Broadcast.broadcast(component, "core.server.alert"));
        });

        instance.getCore().getBridge().registerListener(ServerWhitelistEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getServer().getDisplayName()).space()
                    .primary("has just been").space()
                    .primary(event.getServer().isWhitelisted() ? "whitelisted" : "unwhitelisted").tertiary("!")
                    .build(component -> Broadcast.broadcast(component, "core.server.alert"));
        });
    }
}
