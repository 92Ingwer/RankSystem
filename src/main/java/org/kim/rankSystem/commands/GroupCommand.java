package org.kim.rankSystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.enums.MessageEnum;
import org.kim.rankSystem.enums.PermissionsEnum;
import org.kim.rankSystem.gui.EditGroupGUI;
import org.kim.rankSystem.gui.EditPlayerPermissionsGUI;
import org.kim.rankSystem.objects.PlayerRankObject;
import org.kim.rankSystem.objects.RankObject;
import org.kim.rankSystem.services.GroupService;
import org.kim.rankSystem.services.Methods;
import org.kim.rankSystem.services.PlayerService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GroupCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (args.length == 0) {
            return groupUsages(player);
        }
        //WENN MAN EINE GRUPPE/RANG ERSTELLEN WILL
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 3) {
                player.sendMessage(MessageEnum.CREATE_GROUP_USAGE.getMessage());
                return false;
            }
            String rankName = args[1];
            String displayName = args[2];

            if (SQLCreate.rankExists(rankName)) {
                player.sendMessage(MessageEnum.RANK_EXISTS.getMessage());
                return false;
            }
            RankObject rankObject = new RankObject(rankName, displayName, new ArrayList<>());
            Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
                SQLCreate.createRank(rankObject);
            });
            GroupService.allRanks.add(rankObject);
            player.sendMessage(MessageEnum.SUCESSFULLY_CREATED_GROUP.getMessage());

            return false;
            //WENN MAN ALLES AUFLISTEN MÖCHTE
        } else if (args[0].equalsIgnoreCase("list")) {
            if (args.length != 1) {
                player.sendMessage(MessageEnum.LIST_GROUP_USAGE.getMessage());
                return false;
            }
            player.sendMessage(MessageEnum.LIST_RANK.getMessage());
            for (RankObject rank : GroupService.allRanks) {
                player.sendMessage("§7- " + rank.getRank());
            }
            return false;
            //WENN MAN ES LÖSCHEN WILL
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                player.sendMessage(MessageEnum.DELETE_GROUP_USAGE.getMessage());
                return false;
            }
            String rank = args[1];
            RankObject rankObject = getRankObject(rank);
            if (rankObject == null) {
                player.sendMessage(MessageEnum.RANK_DOES_NOT_EXIST.getMessage());
                return false;
            }
            GroupService.deleteRank(rankObject, player);
            return false;
            //WENN MAN DEN RANG VON EINEM SPIELER SEHEN MÖCHTE
        } else if (args[0].equalsIgnoreCase("info")) {
            if (!PlayerService.hasPermission(player, PermissionsEnum.INFO_GROUPS)) {
                return false;
            }
            if (args.length != 2) {
                player.sendMessage(MessageEnum.GROUP_INFO_USAGE.getMessage());
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(MessageEnum.PLAYER_NOT_FOUND.getMessage());
                return false;
            }
            //ZEIT ANZEIGEN LASSEN
            PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(target.getUniqueId());
            String rank = playerRankObject.getRankObject().getRank();
            List<String> permissionList = playerRankObject.getPermissionList();
            player.sendMessage(MessageEnum.LIST_RANK + " " + rank);
            player.sendMessage(MessageEnum.ADDITIONAL_PERMISSIONS + " " + String.join(", ", permissionList));
            LocalDateTime date = playerRankObject.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
            player.sendMessage(date == null ? MessageEnum.UNTIL_PERMANENT.getMessage() : MessageEnum.UNTIL_TEMPORARY + " " + formatter.format(date));
            return false;
            //WENN MAN EINEM SPIELER EINEN RANG GEBEN MÖCHTE
        } else if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 3 || args.length > 7) {
                player.sendMessage(MessageEnum.GROUP_GIVE_USAGE.getMessage());
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(MessageEnum.PLAYER_NOT_FOUND.getMessage());
                return false;
            }
            String rank = args[2];
            RankObject rankObject = getRankObject(rank);
            if (!GroupService.allRanks.contains(rankObject)) {
                player.sendMessage(MessageEnum.RANK_DOES_NOT_EXIST.getMessage());
                return false;
            }
            PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(target.getUniqueId());
            //WENN MAN ZEIT FESTLEGEN MÖCHTE
            if (args.length > 3) {
                int seks = 0;
                int mins = 0;
                int hours = 0;
                int days = 0;
                try {
                    seks = Integer.parseInt(args[3]);
                    mins = Integer.parseInt(args[4]);
                    hours = Integer.parseInt(args[5]);
                    days = Integer.parseInt(args[6]);
                    if (seks < 0 || mins < 0 || hours < 0 || days < 0) {
                        player.sendMessage("Wrong Numbers");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("Wrong Numbers");
                    return false;
                }
                LocalDateTime until = LocalDateTime.now().plusSeconds(seks).plusMinutes(mins).plusHours(hours).plusDays(days);
                Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
                    SQLCreate.updateUntil(target.getUniqueId(), until);
                });
                playerRankObject.setDate(until);
            }
            playerRankObject.setRankObject(rankObject);
            player.sendMessage(MessageEnum.RANK_SET.getMessage());
            return false;
            //WENN MAN PERMISSIONS EDITIEREN MÖCHTE (MIT GUI)
        } else if (args[0].equalsIgnoreCase("edit")) {
            if (args.length > 4) {
                player.sendMessage(MessageEnum.GROUP_EDIT_MANUELL.getMessage());
                return false;
            }
            if (args.length == 1) {
                EditGroupGUI.openInventory(player);
            }
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(MessageEnum.PLAYER_NOT_FOUND.getMessage());
                    return false;
                }
                EditPlayerPermissionsGUI.openInventory(player, target);
            }
            //WENN MAN ES MANUELL MACHEN MÖCHTE MIT BEFEHLEN
            if (args.length == 4) {
                Player target = Bukkit.getPlayer(args[1]);
                String permission = args[3];
                if (!Bukkit.getOnlinePlayers().contains(target)) {
                    RankObject rankObject = getRankObject(args[1]);
                    if (rankObject == null) {
                        player.sendMessage(MessageEnum.PLAYER_RANK_NOT_FOUND.getMessage());
                        return false;
                    }
                    if (args[2].equalsIgnoreCase("add")) {
                        Methods.addPermission(rankObject, permission);
                        player.sendMessage(MessageEnum.RANK_PERMISSION_ADDED.getMessage());
                    }
                    if (args[2].equalsIgnoreCase("remove")) {
                        Methods.removePermission(rankObject, permission);
                        player.sendMessage(MessageEnum.RANK_PERMISSION_REMOVED.getMessage());
                    }
                    return false;
                }
                if (args[2].equalsIgnoreCase("add")) {
                    Methods.addPermission(player, permission);
                    player.sendMessage(MessageEnum.PLAYER_PERMISSION_ADDED.getMessage());
                }
                if (args[2].equalsIgnoreCase("remove")) {
                    Methods.removePermission(player, permission);
                    player.sendMessage(MessageEnum.PLAYER_PERMISSION_REMOVED.getMessage());
                }
            }
        } else {
            return groupUsages(player);
        }

        return false;
    }
    /**
     * Sends the usage messages for group commands to the player.
     *
     * @param player The player to send the messages to.
     * @return Always returns false.
     */
    public boolean groupUsages(Player player) {
        player.sendMessage(MessageEnum.CREATE_GROUP_USAGE.getMessage());
        player.sendMessage(MessageEnum.LIST_GROUP_USAGE.getMessage());
        player.sendMessage(MessageEnum.DELETE_GROUP_USAGE.getMessage());
        player.sendMessage(MessageEnum.GROUP_INFO_USAGE.getMessage());
        player.sendMessage(MessageEnum.GROUP_GIVE_USAGE.getMessage());
        player.sendMessage(MessageEnum.GROUP_EDIT_USAGE.getMessage());
        return false;
    }

    /**
     * Retrieves a RankObject by its rank name.
     *
     * @param rank The name of the rank.
     * @return The RankObject corresponding to the rank name, or null if not found.
     */
    public static RankObject getRankObject(String rank) {
        for (RankObject rankObject : GroupService.allRanks) {
            if (rankObject.getRank().equals(rank)) {
                return rankObject;
            }
        }
        return null;
    }
}
