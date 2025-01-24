package org.kim.rankSystem.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.kim.rankSystem.commands.GroupCommand;
import org.kim.rankSystem.objects.RankObject;
import org.kim.rankSystem.services.GroupService;
import org.kim.rankSystem.utils.InventoryBuilder;
import org.kim.rankSystem.utils.ItemBuilder;

import java.util.List;

public class EditGroupGUI implements Listener {
    private static Inventory inventory;

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(inventory) && event.getInventory().equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getCurrentItem().getType() == Material.PAPER) {
                String rank = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().displayName());
                RankObject rankObject = GroupCommand.getRankObject(rank);
                EditGroupPermissionsGUI.openInventory((Player) event.getWhoClicked(), rankObject);
                return;
            }
        }
    }

    public static void openInventory(Player player) {
        inventory = getInventory();
        player.openInventory(inventory);
    }

    public static int getFreeSlot(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public static Inventory getInventory() {
        Inventory inventory = new InventoryBuilder("Edit Group", 6 * 9, 1).build();
        for (RankObject rank : GroupService.allRanks) {
            String displayName = rank.getDisplayName();
            int slot = getFreeSlot(inventory);
            inventory.setItem(slot, new ItemBuilder(Material.PAPER).name(Component.text(rank.getRank())).lore(List.of(MiniMessage.miniMessage().deserialize(displayName))).build());
        }
        return inventory;
    }
}