package org.kim.rankSystem.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.enums.MessageEnum;
import org.kim.rankSystem.objects.PlayerRankObject;
import org.kim.rankSystem.objects.RankObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupService {
    public static List<RankObject> allRanks = new ArrayList<>();

    public static void setAllRanks() {
        allRanks = SQLCreate.getAllRanks();
    }

    /**
     * Deletes a rank and updates players who have that rank to the default rank.
     *
     * @param rankObject The rank object to be deleted.
     * @param player     The player who initiated the delete action.
     */
    public static void deleteRank(RankObject rankObject, Player player) {
        if (rankObject.getRank().equals("default")) {
            player.sendMessage(MessageEnum.RANK_PERMISSION_REMOVED.getMessage());
            return;
        }
        if (GroupService.allRanks.contains(rankObject)) {
            GroupService.allRanks.remove(rankObject);
            Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
                SQLCreate.deleteRank(rankObject.getRank());
                for (UUID uuid : SQLCreate.getPlayersWithRank(rankObject.getRank())) {
                    SQLCreate.updatePlayerRank("default", uuid);
                }
                Bukkit.getScheduler().runTask(RankSystem.getInstance(), () -> {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (PlayerRankObject.playerRankObjects.get(target.getUniqueId()).getRankObject().equals(rankObject)) {
                            for (RankObject defaultRank : allRanks) {
                                if (defaultRank.getRank().equals("default")) {
                                    PlayerRankObject.playerRankObjects.get(target.getUniqueId()).setRankObject(defaultRank);
                                }
                            }
                        }
                    }
                });
            });
            player.sendMessage("§cRang gelöscht!");
        } else {
            player.sendMessage("§cDer Rang existiert nicht!");
        }
    }

    /**
     * Schedules a task to check and update player ranks periodically.
     */
    public static void checkGroupScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
                    if (playerRankObject.getRankObject().getRank().equals("default")) {
                        continue;
                    }
                    LocalDateTime localDateTime = playerRankObject.getDate();
                    if (localDateTime == null) {
                        continue;
                    }
                    if (localDateTime.isBefore(LocalDateTime.now())) {
                        for (RankObject rankObject : allRanks) {
                            if (rankObject.getRank().equals("default")) {
                                playerRankObject.setRankObject(rankObject);
                                playerRankObject.setDate(null);
                                Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
                                    SQLCreate.updatePlayerRank(rankObject.getRank(), player.getUniqueId());
                                    SQLCreate.updateUntil(player.getUniqueId(), null);
                                });
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(RankSystem.getInstance(), 0, 100);
    }
}