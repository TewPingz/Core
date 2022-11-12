package me.tewpingz.core.util.inventory;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter(AccessLevel.PROTECTED)
public class PaginatedInv {

    private final String title;
    private final List<PaginatedItem> items = new ArrayList<>();

    private int currentPage = 0;
    private int pageCount = 0;

    public PaginatedInv(String title) {
        this.title = title;
    }

    protected List<PaginatedItem> getPageItems() {
        int startIndex = this.currentPage * 14;
        int endIndex = Math.min(startIndex + 14, this.items.size());
        return this.items.subList(startIndex, endIndex);
    }

    public void addItem(ItemStack itemStack) {
        this.addItem(itemStack, event -> event.setCancelled(true));
    }

    public void addItem(ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        this.items.add(new PaginatedItem(itemStack, consumer));
        this.pageCount = (int) (Math.floor(this.items.size() / 14D)) + 1;
    }

    protected void openNextPage(Player player) {
        this.open(player, this.currentPage + 1);
    }

    protected void openPreviousPage(Player player) {
        this.open(player, this.currentPage - 1);
    }

    public void open(Player player) {
        this.open(player, this.currentPage);
    }

    public void open(Player player, int page) {
        this.currentPage = page;
        new PaginatedPage(this).open(player);
    }
}
