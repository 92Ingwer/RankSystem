package org.kim.rankSystem.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kim.rankSystem.objects.PlayerRankObject;

import java.awt.*;


public class ChatRendererListener implements Listener {
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        PlayerRankObject playerRankObject = PlayerRankObject.playerRankObjects.get(player.getUniqueId());
        String displayname = playerRankObject.getRankObject().getDisplayName();
        Component displayNameComponent = MiniMessage.miniMessage().deserialize(displayname);
        Component playerName = Component.text(player.getName()).color(TextColor.color(Color.GRAY.getRGB()));

        event.renderer((sender,displayName,msg,target) ->
                Component.text()
                        .append(displayNameComponent) // Präfix
                        .append(Component.text(": ")) // Leerzeichen
                        .append(playerName) // Spielername
                        .append(Component.text(": ").color(TextColor.color(0xFFFFFF))) // Trennung
                        .append(msg) // Nachricht
                        .build()
        );
    }
}
