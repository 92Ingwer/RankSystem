package org.kim.rankSystem.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.kim.rankSystem.RankSystem;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.enums.MessageEnum;
import org.kim.rankSystem.services.SignService;

public class InteractWithSignListener implements Listener {
    @EventHandler
    public void onInteractWithSign(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null) {
            return;
        }
        Player player = event.getPlayer();
        if(event.getClickedBlock().getState() instanceof Sign sign && SignService.signPlayers.containsKey(player)) {
            event.setCancelled(true);
            OfflinePlayer target = Bukkit.getOfflinePlayer(SignService.signPlayers.get(player));
            Bukkit.getScheduler().runTaskAsynchronously(RankSystem.getInstance(), () -> {
                String rank = SQLCreate.getRankPlayer(target.getUniqueId());
                Bukkit.getScheduler().runTask(RankSystem.getInstance(), () -> {
                    SignService.signPlayers.remove(player);
                    player.sendMessage(MessageEnum.PLAYER_EDITED_SIGN.getMessage());
                    sign.setLine(0, "[RankSystem]");
                    sign.setLine(1,  player.getName());
                    sign.setLine(2, rank);
                    sign.update();
                });
            });
        }
    }
}
