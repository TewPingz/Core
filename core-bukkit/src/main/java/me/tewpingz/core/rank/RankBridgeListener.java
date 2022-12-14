package me.tewpingz.core.rank;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.rank.event.*;
import me.tewpingz.core.util.Broadcast;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankBridgeListener {

    public RankBridgeListener(CorePlugin instance) {
        instance.getCore().getBridge().registerListener(RankCreateEvent.class, (charSequence, event) -> {
            Core.getInstance().getConfig().getDefaultPalette().toBuilder(false)
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutedBy()).space()
                    .primary("has created a rank named").space()
                    .append(event.getRank().getDisplayNameWithColor())
                    .tertiary(".")
                    .toString(message -> Broadcast.broadcast(message, "core.rank.alert"));
        });

        instance.getCore().getBridge().registerListener(RankUpdateEvent.class, (charSequence, event) -> {
            Core.getInstance().getConfig().getDefaultPalette().toBuilder(false)
                    .tertiary("[Server Monitor]").space()
                    .secondary(event.getExecutedBy()).space()
                    .primary("has updated the rank named").space()
                    .append(event.getRank().getDisplayNameWithColor())
                    .tertiary(".")
                    .toString(message -> Broadcast.broadcast(message, "core.rank.alert"));
        });

        instance.getCore().getBridge().registerListener(RankUpdatePermissionEvent.class, (charSequence, event) -> {
            Core.getInstance().getProfileManager().getCachedProfiles().stream().toList().forEach(profile -> {
                Player player = Bukkit.getPlayer(profile.getPlayerId());
                if (player != null) {
                    boolean hasRank = profile.getActiveGrants().stream().anyMatch(grant -> grant.getRankId().equalsIgnoreCase(event.getRank().getRankId()));
                    if (hasRank) {
                        Profile.ProfileSnapshot realValue = Core.getInstance().getProfileManager().beginCachingOrUpdateProfile(player.getUniqueId());
                        CorePlugin.getInstance().getGrantAttachmentManager().updateAttachment(player, realValue);
                    }
                }
            });
        });

        instance.getCore().getBridge().registerListener(RankDeleteEvent.class, (charSequence, event) -> {
            Core.getInstance().getProfileManager().getCachedProfiles().stream().toList().forEach(profile -> {
                Player player = Bukkit.getPlayer(profile.getPlayerId());
                if (player != null) {
                    boolean hasRank = profile.getActiveGrants().stream().anyMatch(grant -> grant.getRankId().equalsIgnoreCase(event.getRank().getRankId()));
                    if (hasRank) {
                        Core.getInstance().getProfileManager().updateRealProfileAsync(player.getUniqueId(), realValue -> {
                            realValue.getActiveGrants().stream().filter(grant -> grant.getRankSnapshot() == null).toList().forEach(grant -> {
                                realValue.removeGrant(grant, "CONSOLE", "Expired");
                            });
                        }).thenAccept(realValue -> CorePlugin.getInstance().getGrantAttachmentManager().updateAttachment(player, realValue));
                    }
                }
            });
        });

        instance.getCore().getBridge().registerListener(RankPriorityUpdateEvent.class, (charSequence, event) -> {
            Core.getInstance().getProfileManager().getCachedProfiles().stream().toList().forEach(profile -> {
                Player player = Bukkit.getPlayer(profile.getPlayerId());
                if (player != null) {
                    boolean hasRank = profile.getActiveGrants().stream().anyMatch(grant -> grant.getRankId().equalsIgnoreCase(event.getRank().getRankId()));
                    if (hasRank) {
                        Profile.ProfileSnapshot realValue = Core.getInstance().getProfileManager().beginCachingOrUpdateProfile(player.getUniqueId());
                        CorePlugin.getInstance().getGrantAttachmentManager().updateAttachment(player, realValue);
                    }
                }
            });
        });
    }
}
