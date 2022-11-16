package me.tewpingz.core.chat.listener;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.*;
import me.tewpingz.core.util.Broadcast;
import me.tewpingz.message.MessageBuilderDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

public class ChatBridgeListener {

    public ChatBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(StaffChatEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[SC]").space()
                    .tertiary("(%s)".formatted(event.getServer())).space()
                    .secondary(event.getSender()).tertiary(":").space()
                    .primary(event.getMessage())
                    .build(component -> Broadcast.broadcast(component, "core.staffchat"));
        });

        instance.getCore().getBridge().registerListener(AdminChatEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[AC]").space()
                    .tertiary("(%s)".formatted(event.getServer())).space()
                    .secondary(event.getSender()).tertiary(":").space()
                    .primary(event.getMessage())
                    .build(component -> Broadcast.broadcast(component, "core.adminchat"));
        });

        instance.getCore().getBridge().registerListener(PlayerReportEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[Report]").space()
                    .tertiary("(%s)".formatted(event.getServer())).space()
                    .secondary(event.getSender()).space()
                    .primary("has reported").space()
                    .secondary(Core.getInstance().getUuidManager().getName(event.getTarget()).getName()).space()
                    .primary("for").space()
                    .primary(event.getMessage()).tertiary(".")
                    .build(component -> Broadcast.broadcast(component, "core.report.alert"));
        });

        instance.getCore().getBridge().registerListener(PlayerRequestEvent.class, (charSequence, event) -> {
            MessageBuilderDefaults.normal()
                    .tertiary("[Request]").space()
                    .tertiary("(%s)".formatted(event.getServer())).space()
                    .secondary(event.getSender()).space()
                    .primary("has requested for assistance").tertiary(":").space()
                    .primary(event.getMessage()).tertiary(".")
                    .build(component -> Broadcast.broadcast(component, "core.request.alert"));
        });

        instance.getCore().getBridge().registerListener(DiscordChatEvent.class, (charSequence, event) -> {
            if (!CorePlugin.getInstance().getServerInitializer().getConfig().getServerId().equals(event.getServerId())) {
                return;
            }

            MessageBuilderDefaults.normal()
                    .tertiary("[").append(Component.text("Discord").color(TextColor.fromHexString("#7289da"))).tertiary("]").space()
                    .secondary(event.getUsername()).tertiary(":").space()
                    .secondary(event.getMessage())
                    .build(Bukkit::broadcast);
        });
    }

}
