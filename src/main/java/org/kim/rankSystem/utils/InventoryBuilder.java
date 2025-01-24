package org.kim.SkyGens.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.List;

public class InventoryBuilder {
    private final Inventory inventory;

    public InventoryBuilder(String minimessage, int size, int grayglas) {
        Component title = new MiniMessageBuilder(minimessage).get();
        this.inventory = Bukkit.createInventory(null, size, title);
        int sizeforarr = inventory.getSize();
        if (grayglas == 0) return;
        if (grayglas == 1) {
            Integer[] glasslots = {0, 1, 2, 3, 4, 5, 6, 7, 8, sizeforarr - 1, sizeforarr - 2, sizeforarr - 3, sizeforarr - 4, sizeforarr - 5, sizeforarr - 6, sizeforarr - 7, sizeforarr - 8, sizeforarr - 9, sizeforarr - 18, sizeforarr - 27, sizeforarr - 36, sizeforarr - 10, sizeforarr - 19, sizeforarr - 28, sizeforarr, 36, sizeforarr - 46, 9, 17};
            List<Integer> glasslotsList = Arrays.asList(glasslots);
            for (int i = 0; i < inventory.getSize(); i++) {
                if (glasslotsList.contains(i)) {
                    inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(Component.text("")).build());
                }
            }
        }
        if (grayglas == 2) {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(Component.text("")).build());
            }
        }
    }

    public InventoryBuilder aItem(int slot, Material material, Component name, List<Component> lore) {
        inventory.setItem(slot, new ItemBuilder(material).name(name).lore(lore).build());
        return this;
    }

    public InventoryBuilder aHead(int slot, String playername, String name, String lore) {
        inventory.setItem(slot, SkullBuilder.getPlayerHead(playername, name, lore));
        return this;
    }

    public InventoryBuilder aSkull(int slot, String url, String name, String lore) { // Muss noch getestet werden
        inventory.setItem(slot, SkullBuilder.getCustomSkull(url, name, lore));
        return this;
    }

    public Inventory build() {
        return inventory;
    }
}
