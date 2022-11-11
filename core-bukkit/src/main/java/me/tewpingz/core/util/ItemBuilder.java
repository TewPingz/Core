package me.tewpingz.core.util;

import me.tewpingz.core.rank.RankColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Made an item builder class, so I can easily create items lol
 *
 * @author TewPingz
 * @see org.bukkit.inventory.ItemStack
 * @version 1.19.2
 */
public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material) {
        Objects.requireNonNull(material);
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder itemMeta(ItemMeta itemMeta) {
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder name(Component component) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(component);
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder name(String name) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(Component.text(name));
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder color(TextColor color) {
        return this.color(Color.fromRGB(color.red(), color.green(), color.blue()));
    }

    public ItemBuilder color(Color color) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
            this.itemMeta(leatherArmorMeta);
        }
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}
