package org.kim.rankSystem.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.commands.GroupCommand;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.enums.PermissionsEnum;
import org.kim.rankSystem.objects.RankObject;
import org.kim.rankSystem.utils.InventoryBuilder;
import org.kim.rankSystem.utils.ItemBuilder;

import java.awt.*;
import java.util.List;

public class EditGroupPermissionsGUI implements Listener {
    private static Inventory inventory;

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(inventory) && event.getInventory().equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() == null) {
                return;
            }
            if (isSideSlot(event.getSlot())) {
                return;
            }
            String rank = PlainTextComponentSerializer.plainText().serialize(inventory.getItem(4).getItemMeta().displayName());
            RankObject rankObject = GroupCommand.getRankObject(rank);
            List<String> permissionsList = rankObject.getPermissionsList();
            String permission = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().lore().getFirst());
            if(permissionsList.contains(permission)) {
                permissionsList.remove(permission);
            } else {
                permissionsList.add(permission);
            }
            rankObject.setPermissionsList(permissionsList);
            Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
                SQLCreate.updatePermissionRank(rank,permissionsList);
                Bukkit.getScheduler().runTask(RankSystem.getInstance(), () -> {
                    player.openInventory(getInventory(rankObject));
                });
            });
        }
    }

    public static void openInventory(Player player, RankObject rankObject) {
        Inventory inventory = getInventory(rankObject);
        player.openInventory(inventory);
    }

    public static Inventory getInventory(RankObject rankObject) {
        inventory = new InventoryBuilder("Edit Rank", 6 * 9, 1).aItem(4, Material.PAPER, Component.text(rankObject.getRank()), null)
                .build();
        List<String> permissionsList = rankObject.getPermissionsList();
        return EditPlayerPermissionsGUI.setPermissionsInGUI(permissionsList, inventory);
    }
    public static boolean isSideSlot(int slot) {
        return slot/9 == 0 || slot%9 == 0 || slot%9 == 8 || slot/9 == 5;
    }
}
