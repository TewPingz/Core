package me.tewpingz.core.server.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.tewpingz.core.Core;
import me.tewpingz.core.server.Server;
import me.tewpingz.core.util.ItemBuilder;
import me.tewpingz.core.util.inventory.PaginatedInv;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@CommandAlias("servers")
@CommandPermission("core.servers")
public class ServersCommand extends BaseCommand {
    @Default
    public void onCommand(Player player) {
        new ServersInventory().open(player);
    }

    private static class ServersInventory extends PaginatedInv {
        public ServersInventory() {
            super(ChatColor.GOLD + "Servers");

            for (Server.ServerSnapshot server : Core.getInstance().getServerManager().getCachedValues()) {
                if (!server.isOnline()) {
                    continue;
                }
                ItemStack itemStack = new ItemBuilder(Material.NETHER_STAR)
                        .enchants(Enchantment.ARROW_INFINITE)
                        .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE)
                        .name(Component.text(server.getDisplayName()).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        .lore("")
                        .lore("&6Current Players&f: %s".formatted(server.getPlayers().size()))
                        .lore("&6Max Players&f: %s".formatted(server.getMaxPlayers()))
                        .lore("&6Whitelisted&f: %s".formatted(server.isWhitelisted() ? "Yes" : "No"))
                        .lore("")
                        .build();

                this.addItem(itemStack);
            }
        }
    }
}
