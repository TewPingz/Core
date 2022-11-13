package me.tewpingz.core.chat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.tewpingz.core.CorePlugin;
import me.tewpingz.core.util.TimeUtil;
import me.tewpingz.message.MessageBuilderDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.time.Duration;

@CommandAlias("slowchat")
@CommandPermission("core.slowchat")
public class SlowChatCommand extends BaseCommand {
    @Default
    @Syntax("<duration>")
    @CommandCompletion("0s:10s:30s:1m:10m")
    public void onCommand(CommandSender commandSender, Duration duration) {
        if (duration.isNegative()) {
            MessageBuilderDefaults.error()
                    .primary("You cannot permanently mute chat").tertiary("!")
                    .build(commandSender::sendMessage);
        }
        CorePlugin.getInstance().getChatManager().setChatSlow(duration.toMillis());

        if (duration.isZero()) {
            MessageBuilderDefaults.normal()
                    .secondary(commandSender.getName()).space()
                    .primary("has unslowed chat").tertiary(".")
                    .build(Bukkit::broadcast);
        } else {
            MessageBuilderDefaults.normal()
                    .secondary(commandSender.getName()).space()
                    .primary("has slowed chat by").space()
                    .secondary(TimeUtil.formatLongIntoDetailedString(duration.toMillis())).tertiary(".")
                    .build(Bukkit::broadcast);
        }
    }
}
