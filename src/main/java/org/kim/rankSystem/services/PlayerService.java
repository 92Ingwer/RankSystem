package org.kim.rankSystem.services;

import org.bukkit.entity.Player;
import org.kim.rankSystem.enums.MessageEnum;
import org.kim.rankSystem.enums.PermissionsEnum;
import org.kim.rankSystem.objects.PlayerRankObject;

import java.util.ArrayList;
import java.util.List;

public class PlayerService {
    public static boolean hasPermission(Player player, PermissionsEnum permissionsEnum) {
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        List<String> playerPermissionList = playerRankObject.getPermissionList();
        List<String> rankPermissionList = playerRankObject.getRankObject().getPermissionsList();
        List<String> combinedPermissionList = new ArrayList<>();
        combinedPermissionList.addAll(playerPermissionList);
        combinedPermissionList.addAll(rankPermissionList);
        if (!combinedPermissionList.contains(permissionsEnum.getPermission())) {
            player.sendMessage(MessageEnum.NOT_PERMISSION.getMessage());
            return false;
        }
        return true;
    }
}
