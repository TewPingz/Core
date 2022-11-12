package me.tewpingz.core.util;

import me.tewpingz.core.rank.RankColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
        Objects.requireNonNull(itemMeta);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder name(Component component) {
        Objects.requireNonNull(component);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(component);
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder name(String name) {
        Objects.requireNonNull(name);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.displayName(Component.text(name));
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder lore(String line) {
        Objects.requireNonNull(line);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        itemMeta.setLore(lore);
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder lore(List<Component> lore) {
        Objects.requireNonNull(lore);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.lore(lore);
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder lore(Component line) {
        Objects.requireNonNull(line);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        List<Component> lore = itemMeta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(line);
        itemMeta.lore(lore);
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder color(TextColor color) {
        Objects.requireNonNull(color);
        return this.color(Color.fromRGB(color.red(), color.green(), color.blue()));
    }

    public ItemBuilder color(Color color) {
        Objects.requireNonNull(color);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
            this.itemMeta(leatherArmorMeta);
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        Objects.requireNonNull(flags);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(flags);
        return this.itemMeta(itemMeta);
    }

    public ItemBuilder enchants(Enchantment enchantment) {
        return this.enchants(enchantment, 1);
    }

    public ItemBuilder enchants(Enchantment enchantment, int level) {
        return this.enchants(enchantment, level, true);
    }

    public ItemBuilder enchants(Enchantment enchantment, int level, boolean ignoreLevelCap) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, ignoreLevelCap);
        return this.itemMeta(itemMeta);
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public void build(Consumer<ItemStack> consumer) {
        consumer.accept(this.build());
    }
}
