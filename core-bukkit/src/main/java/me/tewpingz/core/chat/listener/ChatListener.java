package me.tewpingz.core.chat.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.ChatManager;
import me.tewpingz.core.chat.ServerChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final ChatManager chatManager;
    private final PlainTextComponentSerializer plainTextComponentSerializer = PlainTextComponentSerializer.plainText();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChatMonitor(AsyncChatEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), () -> {
            String serverId = CorePlugin.getInstance().getServerInitializer().getConfig().getServerId();
            String name = event.getPlayer().getName();
            String message = plainTextComponentSerializer.serialize(event.message());
            Core.getInstance().getBridge().callEvent(new ServerChatEvent(event.getPlayer().getUniqueId(), serverId, name, message));
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("core.mutechat.bypass") && !this.chatManager.isChatEnabled()) {
            event.setCancelled(true);
            Core.getInstance().getConfig().getErrorPalette().toBuilder()
                    .primary("Chat is currently disabled").tertiary("!")
                    .build(player::sendMessage);
            return;
        }

        if (this.chatManager.getChatSlow() < 0 || event.getPlayer().hasPermission("core.slowchat.bypass")) {
            return;
        }

        if (!player.hasMetadata("lastMessageTime")) {
            player.setMetadata("lastMessageTime", new FixedMetadataValue(CorePlugin.getInstance(), System.currentTimeMillis()));
            return;
        }

        long lastMessageTime = player.getMetadata("lastMessageTime").get(0).asLong();

        if (System.currentTimeMillis() - lastMessageTime < this.chatManager.getChatSlow()) {
            event.setCancelled(true);
            Core.getInstance().getConfig().getErrorPalette().toBuilder()
                    .primary("You are talking too quickly.").space()
                    .tertiary("(Chat is currently slowed!)")
                    .build(player::sendMessage);
            return;
        }

        player.setMetadata("lastMessageTime", new FixedMetadataValue(CorePlugin.getInstance(), System.currentTimeMillis()));

    }
}
