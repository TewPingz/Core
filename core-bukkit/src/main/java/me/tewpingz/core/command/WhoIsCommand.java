package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.util.uuid.AsyncUuid;
import org.bukkit.command.CommandSender;

import java.util.Date;

@CommandPermission("core.whois")
@CommandAlias("whois")
public class WhoIsCommand extends BaseCommand {
    @Default
    @Syntax("<target>")
    @CommandCompletion("@players")
    public void onCommand(CommandSender sender, AsyncUuid asyncUuid) {
        asyncUuid.fetchUuid(sender, uuid -> {
            Profile.ProfileSnapshot profile = Core.getInstance().getProfileManager().getRealProfile(uuid);

            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .primary("This is the profile information for").space()
                    .secondary(profile.getLastSeenName()).tertiary("!")
                    .build(sender::sendMessage);

            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .space().tertiary("-").space()
                    .primary("First seen").tertiary(":").space()
                    .secondary(profile.getJoinTime() == -1 ? "Never" : new Date(profile.getJoinTime()).toString())
                    .build(sender::sendMessage);

            if (profile.getJoinTime() != -1) {
                Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                        .space().tertiary("-").space()
                        .primary("Last seen").tertiary(":").space()
                        .secondary(profile.getLastSeen() == -1 ? "Online" : new Date(profile.getLastSeen()).toString())
                        .build(sender::sendMessage);
            }

            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .space().tertiary("-").space()
                    .primary("Banned").tertiary(":").space()
                    .secondary(profile.getBan() == null ? "No" : "Yes")
                    .build(sender::sendMessage);

            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .space().tertiary("-").space()
                    .primary("Muted").tertiary(":").space()
                    .secondary(profile.getMute() == null ? "No" : "Yes")
                    .build(sender::sendMessage);

            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .space().tertiary("-").space()
                    .primary("Blacklisted").tertiary(":").space()
                    .secondary(profile.getBlacklist() == null ? "No" : "Yes")
                    .build(sender::sendMessage);

            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .space().tertiary("-").space()
                    .primary("Display Rank").tertiary(":").space()
                    .append(profile.getDisplayRank().getDisplayNameWithColor())
                    .build(sender::sendMessage);

            Core.getInstance().getConfig().getDefaultPalette().toBuilder()
                    .space().tertiary("-").space()
                    .primary("Discord ID").tertiary(":").space()
                    .secondary(profile.getDiscordId() == null ? "N/A" : profile.getDiscordId())
                    .build(sender::sendMessage);
        });
    }
}
