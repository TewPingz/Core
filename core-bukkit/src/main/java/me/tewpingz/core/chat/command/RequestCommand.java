package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.chat.PlayerRequestEvent;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.entity.Player;

@CommandAlias("request")
@CommandPermission("core.request")
public class RequestCommand extends BaseCommand {
    @Default
    @Syntax("<message>")
    public void onCommand(Player player, String message) {
        Core.getInstance().getProfileManager().updateRealValueAsync(player.getUniqueId(), profile -> {
            if (!(profile.getLastRequestExecuted() == -1 || (System.currentTimeMillis() - profile.getLastRequestExecuted()) > 30_000)) {
                MessageBuilderDefaults.error()
                        .primary("You are currently on request cooldown")
                        .tertiary(".")
                        .build(player::sendMessage);
                return;
            }

            profile.setLastRequestExecuted(System.currentTimeMillis());
            String server = CorePlugin.getInstance().getServerInitializer().getServerDisplayName();
            Core.getInstance().getBridge().callEvent(new PlayerRequestEvent(player.getName(), server, message));
        });
    }
}
