package me.tewpingz.core.profile.grant;

import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GrantAttachmentManager {

    private final Map<UUID, PermissionAttachment> attachmentMap;

    public GrantAttachmentManager() {
        this.attachmentMap = new HashMap<>();
    }

    public void updateAttachment(Player player, Profile.ProfileSnapshot profile) {
        Set<String> permissions = profile.getDisplayRank().getEffectivePermissions();
        Bukkit.getScheduler().runTask(CorePlugin.getInstance(), () -> {
            PermissionAttachment attachment = player.addAttachment(CorePlugin.getInstance());
            permissions.forEach(permission -> attachment.setPermission(permission, true));
            CorePlugin.getInstance().getGrantAttachmentManager().track(player.getUniqueId(), attachment);
            player.recalculatePermissions();
            player.updateCommands();
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
