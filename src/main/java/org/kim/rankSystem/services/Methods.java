package org.kim.rankSystem.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.enums.PermissionsEnum;
import org.kim.rankSystem.objects.PlayerRankObject;
import org.kim.rankSystem.objects.RankObject;

import java.util.List;
import java.util.logging.Logger;

public class Methods {

    public static void addPermission(Player player, String permission) {
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        if(permission.equals("*")) {
            addAllPermissions(player);
            return;
        }
        if(!isPermissionValid(permission) || hasPlayerPermission(player, permission)) {
            return;
        }
        playerRankObject.getPermissionList().add(permission);
        List<String> permissionsList = playerRankObject.getPermissionList();
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
            SQLCreate.updatePermissionPlayer(player.getUniqueId(), permissionsList);
        });
    }
    public static void addPermission(RankObject rankObject, String permission) {
        if(permission.equals("*")) {
            addAllPermissions(rankObject);
            return;
        }
        if(!isPermissionValid(permission) || hasRankPermission(rankObject, permission)) {
            return;
        }
        List<String> permissionsList = rankObject.getPermissionsList();
        permissionsList.add(permission);
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
            SQLCreate.updatePermissionRank(rankObject.getRank(), permissionsList);
            GroupService.setAllRanks();
        });
    }
    public static void removePermission(Player player, String permission) {
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        if(permission.equals("*")) {
            removeAllPermissions(player);
            return;
        }
        if(!isPermissionValid(permission) || !hasPlayerPermission(player, permission)) {
            return;
        }
        List<String> permissionsList = playerRankObject.getPermissionList();
        permissionsList.remove(permission);
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
            SQLCreate.updatePermissionPlayer(player.getUniqueId(), permissionsList);
        });
    }
    public static void removePermission(RankObject rankObject, String permission) {
        if(permission.equals("*")) {
            removeAllPermissions(rankObject);
            return;
        }
        if(!isPermissionValid(permission) || !hasRankPermission(rankObject, permission)) {
            return;
        }
        List<String> permissionsList = rankObject.getPermissionsList();
        permissionsList.remove(permission);
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
            SQLCreate.updatePermissionRank(rankObject.getRank(), permissionsList);
            GroupService.setAllRanks();
        });
    }
    public static boolean isPermissionValid(String permission) {
        for(PermissionsEnum permissionsEnum : PermissionsEnum.values()) {
            if(permissionsEnum.getPermission().equals(permission)) {
                return true;
            }
        }
        return false;
    }
    public static boolean hasPlayerPermission(Player player, String permission) {
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        List<String> playerPermissionList = playerRankObject.getPermissionList();
        return playerPermissionList.contains(permission);
    }
    public static boolean hasRankPermission(RankObject rankObject, String permission) {
        List<String> rankPermissionList = rankObject.getPermissionsList();
        return rankPermissionList.contains(permission);
    }
    public static void removeAllPermissions(Player player) {
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        List<String> permissionsList = playerRankObject.getPermissionList();
        permissionsList.clear();
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
            SQLCreate.updatePermissionPlayer(player.getUniqueId(), permissionsList);
        });
    }
    public static void removeAllPermissions(RankObject rankObject) {
        List<String> permissionsList = rankObject.getPermissionsList();
        permissionsList.clear();
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
            SQLCreate.updatePermissionRank(rankObject.getRank(), permissionsList);
            GroupService.setAllRanks();
        });
    }
    public static void addAllPermissions(Player player) {
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        List<String> permissionsList = playerRankObject.getPermissionList();
        permissionsList.clear();
        for(PermissionsEnum permissionsEnum : PermissionsEnum.values()) {
            permissionsList.add(permissionsEnum.getPermission());
        }
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(),() -> {
            SQLCreate.updatePermissionPlayer(player.getUniqueId(), permissionsList);
        });
    }

    public static void addAllPermissions(RankObject rankObject) {
        List<String> permissionsList = rankObject.getPermissionsList();
        permissionsList.clear();
        for (PermissionsEnum permissionsEnum : PermissionsEnum.values()) {
            permissionsList.add(permissionsEnum.getPermission());
        }
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
            SQLCreate.updatePermissionRank(rankObject.getRank(), permissionsList);
            GroupService.setAllRanks();
        });
    }
}
