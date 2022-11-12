package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.punishment.PunishmentProcedure;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("ban")
@CommandPermission("core.ban")
public class BanCommand extends BaseCommand {
    @Default
    public void onCommand(Player player, AsyncUuid asyncUuid) {
        asyncUuid.fetchUuidAsync().thenAccept(uuid -> {
            if (uuid == null) {
                MessageBuilderDefaults.error()
                        .secondary(asyncUuid.getName()).space()
                        .primary("does not exist as a player")
                        .tertiary("!")
                        .build(player::sendMessage);
                return;
            }
            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> new PunishmentProcedure(uuid, PunishmentType.BAN).start(player));
        });
    }
}
