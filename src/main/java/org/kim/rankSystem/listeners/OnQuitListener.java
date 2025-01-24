package org.kim.rankSystem.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.objects.PlayerRankObject;

import java.util.List;
import java.util.UUID;

public class OnQuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(uuid);
        //playerrankobject
        String rank = playerRankObject.getRankObject().getRank();
        List<String> permissionList = playerRankObject.getPermissionList();
        updatePlayerRank(rank, permissionList, uuid);
    }
    public void updatePlayerRank(String rank, List<String> permissionList, UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
            SQLCreate.updatePlayerRanks(uuid, rank, permissionList);
        });
    }
}
