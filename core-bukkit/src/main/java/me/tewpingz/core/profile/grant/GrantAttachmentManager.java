package me.tewpingz.core.profile.grant;

import me.tewpingz.core.Core;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import me.tewpingz.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public class GrantAttachmentManager {

    private final Map<UUID, PermissionAttachment> attachmentMap;

    public GrantAttachmentManager() {
        this.attachmentMap = new HashMap<>();
    }

    public void createAttachment(Player player, Profile.ProfileSnapshot profile) {
        Set<String> permissions = new HashSet<>();
        Set<String> inherits = new HashSet<>();

        profile.getSortedActiveGrants().forEach(grant -> {
            permissions.addAll(grant.getRankSnapshot().getPermissions());
            inherits.addAll(grant.getRankSnapshot().getInherits());
        });

        inherits.forEach(rankId -> {
            Rank.RankSnapshot rank = Core.getInstance().getRankManager().getRank(rankId);
            permissions.addAll(rank.getPermissions());
        });

        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
            PermissionAttachment attachment = player.addAttachment(CorePlugin.getInstance());
            permissions.forEach(permission -> attachment.setPermission(permission, true));
            CorePlugin.getInstance().getGrantAttachmentManager().track(player.getUniqueId(), attachment);
        });
    }

    public void track(UUID uuid, PermissionAttachment attachment) {
        PermissionAttachment previous = this.attachmentMap.put(uuid, attachment);
        if (previous != null) {
            previous.remove();
        }
    }

    public void untrack(UUID uuid) {
        this.attachmentMap.remove(uuid);
    }
}
