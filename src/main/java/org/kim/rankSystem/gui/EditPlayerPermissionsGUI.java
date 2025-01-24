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
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.enums.PermissionsEnum;
import org.kim.rankSystem.objects.PlayerRankObject;
import org.kim.rankSystem.utils.InventoryBuilder;
import org.kim.rankSystem.utils.ItemBuilder;

import java.awt.*;
import java.util.List;

public class EditPlayerPermissionsGUI implements Listener {
    private static Inventory inventory;
    private static Player otherPlayer;
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(inventory) && event.getInventory().equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() == null) {
                return;
            }
            if (EditGroupPermissionsGUI.isSideSlot(event.getSlot())) {
                return;
            }
            PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(otherPlayer.getUniqueId());
            List<String> permissionsList = playerRankObject.getPermissionList();
            String permission = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().lore().getFirst());
            if(permissionsList.contains(permission)) {
                permissionsList.remove(permission);
            } else {
                permissionsList.add(permission);
            }
            playerRankObject.setPermissionList(permissionsList);
            Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
                SQLCreate.updatePermissionPlayer(otherPlayer.getUniqueId(),permissionsList);
                Bukkit.getScheduler().runTask(RankSystem.getInstance(),() -> {
                    openInventory(player,otherPlayer);
                });
            });

        }
    }
    public static void openInventory(Player player, Player target) {
        otherPlayer = target;
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        Inventory inventory = getInventory(player, playerRankObject);
        player.openInventory(inventory);
    }
    public static Inventory getInventory(Player player,PlayerRankObject playerRankObject) {
        inventory = new InventoryBuilder("Edit Player Permissions", 6 * 9, 1).aItem(4, Material.PAPER, Component.text(player.getName()), null)
                .build();
        List<String> permissionsList = playerRankObject.getPermissionList();
        return setPermissionsInGUI(permissionsList, inventory);
    }

    public static Inventory setPermissionsInGUI(List<String> permissionsList, Inventory inventory) {
        for (PermissionsEnum permissionsEnum : PermissionsEnum.values()) {
            int slot = EditGroupGUI.getFreeSlot(inventory);
            Component name = Component.text(permissionsEnum.getName()).color(TextColor.color(Color.RED.getRGB()));
            Material material = permissionsEnum.getDisplayedMaterial();
            List<Component> lore = List.of(Component.text(permissionsEnum.getPermission()));
            if (permissionsList.contains(permissionsEnum.getPermission())) {
                name = Component.text(permissionsEnum.getName()).color(TextColor.color(Color.GREEN.getRGB()));
            }
            inventory.setItem(slot, new ItemBuilder(material).name(name).lore(lore).build());
        }
        return inventory;
    }
}
