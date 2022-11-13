package me.tewpingz.core.profile.punishment.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.punishment.PunishmentProcedure;
import me.tewpingz.core.profile.punishment.PunishmentType;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("blacklist")
@CommandPermission("core.blacklist")
public class BlacklistCommand extends BaseCommand {
    @Default
    @Syntax("<target>")
    @CommandCompletion("@players")
    public void onCommand(Player player, AsyncUuid asyncUuid) {
        asyncUuid.fetchUuid(player, uuid -> {
            Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> new PunishmentProcedure(uuid, PunishmentType.BLACKLIST).start(player));
        });
    }
}