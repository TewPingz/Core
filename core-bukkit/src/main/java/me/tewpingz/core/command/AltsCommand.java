package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.profile.alt.AltEntry;
import me.tewpingz.core.util.uuid.AsyncUuid;
import me.tewpingz.message.MessageBuilder;
import me.tewpingz.message.MessageBuilderDefaults;
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
            Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getRealValue(uuid);
            String lastIp = profile.getLastIp();

            if (lastIp == null) {
                MessageBuilderDefaults.error().secondary(asyncUuid.getName()).space()
                        .primary("does not have a previous ip on the server").space()
                        .tertiary("(Have they joined the server before?)")
                        .build(sender::sendMessage);
                return;
            }

            AltEntry.AltProfileSnapshot altEntry = Core.getInstance().getAltManager().getAlts(lastIp);
            MessageBuilder builder = MessageBuilderDefaults.normal();
            Collection<UUID> alts = altEntry.getRelatedIds().stream().filter(altId -> !altId.equals(uuid)).toList();

            if (alts.size() > 0) {
                for (UUID relatedId : alts) {
                    if (!builder.isEmpty()) {
                        builder.tertiary(",").space();
                    }
                    builder.primary(Core.getInstance().getUuidManager().getName(relatedId));
                }

                MessageBuilderDefaults.normal().primary("There are").space()
                        .secondary(alts.size()).space()
                        .primary("Alts for").space()
                        .secondary(profile.getLastSeenName()).tertiary(":")
                        .append(builder.build())
                        .build(sender::sendMessage);
            } else {
                MessageBuilderDefaults.error().secondary(profile.getLastSeenName()).space()
                        .primary("has not alts")
                        .build(sender::sendMessage);
            }
        });
    }
}
