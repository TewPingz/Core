package me.tewpingz.core.util.uuid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.tewpingz.core.Core;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class AsyncUuid {

    private final String name;

    public void fetchUuid(CommandSender executor, Consumer<UUID> consumer) {
        if (name.length() > 16) {
            this.promptPlayerDoesNotExist(executor);
            return;
        }

        UuidManager.NameToUuidEntry.NameToUuidSnapshot entry = Core.getInstance().getUuidManager().getUuid(this.name);

        if (entry.getUuid() == null) {
            this.promptPlayerDoesNotExist(executor);
            return;
        }

        consumer.accept(entry.getUuid());
    }

    private void promptPlayerDoesNotExist(CommandSender executor) {
        Core.getInstance().getConfig().getErrorPalette().toBuilder()
                .secondary(this.name).space()
                .primary("does not exist as a player")
                .tertiary("!")
                .build(executor::sendMessage);
    }
}
