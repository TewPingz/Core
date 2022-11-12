package me.tewpingz.core.util.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class PaginatedItem {

    private final ItemStack itemStack;
    private final Consumer<InventoryClickEvent> consumer;

}
