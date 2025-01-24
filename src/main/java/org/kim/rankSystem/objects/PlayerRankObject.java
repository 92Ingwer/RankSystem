package org.kim.rankSystem.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.scoreboard.TablistService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerRankObject {
    public static final HashMap<UUID, PlayerRankObject> playerRankObjects = new HashMap<>();
    private RankObject rankObject;
    private UUID uuid;
    private List<String> permissionList;
    private LocalDateTime date;

    public PlayerRankObject(UUID uuid, RankObject rankObject, List<String> permissionList, LocalDateTime date) {
        this.rankObject = rankObject;
        this.uuid = uuid;
        this.permissionList = permissionList;
        this.date = date;
    }
    public void setRankObject(RankObject rankObject) {
        this.rankObject = rankObject;
        TablistService.setTabPrefix(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
            SQLCreate.updatePlayerRank(rankObject.getRank(),uuid);
        });
    }
}
