package org.kim.rankSystem.scoreboard;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.kim.rankSystem.objects.PlayerRankObject;

import java.util.UUID;

public class TablistService {
    public static void setTabPrefix(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(uuid);
        String rank = playerRankObject.getRankObject().getRank();
        String displayname = playerRankObject.getRankObject().getDisplayName();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(rank);
        if (team == null) {
            team = scoreboard.registerNewTeam(rank);
        }
        team.prefix(MiniMessage.miniMessage().deserialize(displayname + ": "));
        team.addEntry(player.getName());
    }
}
