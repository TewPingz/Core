package me.tewpingz.core.profile;

import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import me.tewpingz.core.profile.alt.AltEntry;
import me.tewpingz.core.profile.punishment.Punishment;
import me.tewpingz.core.util.HashUtil;
import me.tewpingz.core.util.uuid.UuidManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final UuidManager uuidManager;
    private final ProfileManager profileManager;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        this.uuidManager.updateRealValues(event.getUniqueId(), event.getName());
        String hashedIp = HashUtil.hash(event.getAddress().getHostAddress());
        AltEntry.AltProfileSnapshot altEntry = Core.getInstance().getAltManager().addUuid(hashedIp, uuid);
        for (UUID relatedId : altEntry.getRelatedIds()) {
            Profile.ProfileSnapshot relatedProfile = this.profileManager.getRealValue(relatedId);
            if (relatedProfile.getBlacklist() != null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text("Your account is linked to account that is blacklisted").color(NamedTextColor.RED));
                return;
            }
        }

        Profile.ProfileSnapshot fetchedProfile = this.profileManager.updateRealValue(uuid, profile -> {
            if (profile.getJoinTime() == -1) {
                profile.setJoinTime(System.currentTimeMillis());
            }
            profile.setLastSeenName(event.getName());
            profile.setLastSeen(-1);
            profile.setLastIp(hashedIp);
            profile.getActivePunishments().stream().filter(Punishment::hasExpired).toList().forEach(punishment -> {
                profile.removePunishment(punishment, "CONSOLE", "Expired");
            });
        });

        if (fetchedProfile.getBlacklist() != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text("You are currently blacklisted").color(NamedTextColor.RED));
            return;
        }

        if (fetchedProfile.getBan() != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Component.text("You are currently banned").color(NamedTextColor.RED));
            return;
        }

        this.uuidManager.beginCachingLocally(event.getUniqueId(), event.getName());
        this.profileManager.beginCachingLocally(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLoginMonitor(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            // Make sure to unload the profile from cache if the login is cancelled.
            this.profileManager.stopCachingLocally(event.getUniqueId());
            this.uuidManager.stopCachingLocally(event.getUniqueId(), event.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        this.profileManager.stopCachingLocally(uuid);
        this.uuidManager.stopCachingLocally(uuid, player.getName());
        this.profileManager.updateRealValueAsync(uuid, profile -> profile.setLastSeen(System.currentTimeMillis()));
    }
}
