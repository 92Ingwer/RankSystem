package org.kim.rankSystem.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.objects.PlayerRankObject;
import org.kim.rankSystem.objects.RankObject;
import org.kim.rankSystem.scoreboard.Scoreboards;
import org.kim.rankSystem.scoreboard.TablistService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
            boolean userExists = SQLCreate.userExists(uuid);
            if (!PlayerRankObject.playerRankObjects.containsKey(uuid)) {
                RankObject rankObject = new RankObject("default", "DEFAULT", new ArrayList<>());
                PlayerRankObject playerRankObject = new PlayerRankObject(uuid, rankObject, new ArrayList<>(), null);
                PlayerRankObject.playerRankObjects.put(uuid, playerRankObject);
            }
            if (!userExists) {
                SQLCreate.insertUser(player.getUniqueId());
            } else {
                //PlayerRankObject
                List<String> playerPermissionsList = SQLCreate.getPermissionsPlayer(uuid);
                String rank = SQLCreate.getRankPlayer(uuid);
                String displayName = SQLCreate.getDisplayname(rank);
                List<String> rankPermissionsList = SQLCreate.getPermissionRank(rank);
                RankObject rankObject = new RankObject(rank, displayName, rankPermissionsList);
                PlayerRankObject playerRankObject = new PlayerRankObject(uuid, rankObject, playerPermissionsList, SQLCreate.getUntil(uuid));
                PlayerRankObject.playerRankObjects.put(uuid, playerRankObject);
            }
            TablistService.setTabPrefix(uuid);
            Bukkit.getScheduler().runTask(RankSystem.getInstance(), () -> {
                Scoreboards.showScoreBoard(player);
            });
        });
    }
}
