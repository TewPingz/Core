package me.tewpingz.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

@CommandPermission("core.sync")
@CommandAlias("sync|discordsync")
public class SyncCommand extends BaseCommand {
    @Default
    public void onCommand(Player player) {
        Core.getInstance().getProfileManager().updateRealProfileAsync(player.getUniqueId(), profile -> {
            if (!(profile.getSyncCooldown() == -1 || (profile.getSyncCooldown() - System.currentTimeMillis()) < 0)) {
                MessageBuilderDefaults.error()
                        .primary("You are currently on sync cooldown for").space()
                        .secondary(TimeUtil.formatLongIntoDetailedString(profile.getSyncCooldown() - System.currentTimeMillis()))
                        .tertiary(".")
                        .build(player::sendMessage);
                return;
            }

            profile.setSyncCooldown(System.currentTimeMillis() + (60_000 * 5));
            profile.setSyncCode(this.getRandom());
            MessageBuilderDefaults.success()
                    .primary("Your sync code is").space()
                    .secondary(profile.getSyncCode()).tertiary(".").space()
                    .primary("Go into the discord and use it in the sync channel").tertiary("!")
                    .build(player::sendMessage);
        });
    }

    private String getRandom() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            builder.append(ThreadLocalRandom.current().nextInt(9));
        }
        return builder.toString();
    }
}
