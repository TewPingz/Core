package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.alt.AltEntry;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilder;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.UUID;

@CommandAlias("alts|accounts")
@CommandPermission("core.alts")
public class AltsCommand extends BaseCommand {
    @Default
    @CommandCompletion("@players")
    @Syntax("<target>")
    public void onCommand(CommandSender sender, AsyncUuid asyncUuid) {
        asyncUuid.fetchUuid(sender, uuid -> {
            Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getRealProfile(uuid);
            String lastIp = profile.getLastIp();

            if (lastIp == null) {
                Core.getInstance().getConfig().getErrorPalette().toBuilder()
                        .secondary(asyncUuid.getName()).space()
                        .primary("does not have a previous ip on the server").space()
                        .tertiary("(Have they joined the server before?)")
                        .build(sender::sendMessage);
                return;
            }

            AltEntry.AltProfileSnapshot altEntry = Core.getInstance().getAltManager().getAlts(lastIp);
            MessageBuilder builder = Core.getInstance().getConfig().getDefaultPalette().toBuilder();
            Collection<UUID> alts = altEntry.getRelatedIds().stream().filter(altId -> !altId.equals(uuid)).toList();

            if (alts.size() > 0) {
                for (UUID relatedId : alts) {
                    if (!builder.isEmpty()) {
                        builder.tertiary(",").space();
                    }
                    builder.primary(Core.getInstance().getUuidManager().getName(relatedId).getName());
                }

                Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                        .primary("There are").space()
                        .secondary(alts.size()).space()
                        .primary("Alts for").space()
                        .secondary(profile.getLastSeenName()).tertiary(":").space()
                        .append(builder.build())
                        .build(sender::sendMessage);
            } else {
                Core.getInstance().getConfig().getErrorPalette().toBuilder()
                        .secondary(profile.getLastSeenName()).space()
                        .primary("has not alts")
                        .build(sender::sendMessage);
            }
        });
    }
}
