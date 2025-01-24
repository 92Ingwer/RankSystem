package org.kim.rankSystem.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.objects.PlayerRankObject;

public class Scoreboards {
    public static void showScoreBoard(Player player) {
        String rankName = PlayerRankObject.playerRankObjects.get(player.getUniqueId()).getRankObject().getRank();
        String plainRankName = PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(rankName));
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        Objective objective = board.getObjective("PlayerGroup");
        if (objective == null) {
            objective = board.registerNewObjective("PlayerGroup", "dummy", ChatColor.AQUA + "Your Group");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        String rankScoreName = "Rang: " + plainRankName;
        Score rankScore = objective.getScore(rankScoreName);
        rankScore.setScore(1);
        for (String entry : objective.getScoreboard().getEntries()) {
            if (!entry.equals(rankScoreName)) {
                objective.getScoreboard().resetScores(entry);
            }
        }

        player.setScoreboard(board);
    }


    public static void updateScoreboard() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    showScoreBoard(player);
                }
            }
        }.runTaskTimer(RankSystem.getInstance(), 0L, 100L);
    }
}
