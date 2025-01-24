package org.kim.rankSystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kim.rankSystem.enums.MessageEnum;
import org.kim.rankSystem.services.SignService;

import java.util.UUID;

public class SetSignCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player player)) {
            return false;
        }
        if(strings.length != 1) {
            player.sendMessage(MessageEnum.SET_SIGN.getMessage());
            return false;
        }
        //Spieler wird durch UUID geholt
        UUID uuid = Bukkit.getOfflinePlayer(strings[0]).getUniqueId();
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

        if(!target.hasPlayedBefore()) {
            player.sendMessage(MessageEnum.PLAYER_NEVER_ONLINE.getMessage());
            return false;
        }
        //Packt in eine Hashmap, die dann überprüft, ob der Spieler es ausgeführt hat
        SignService.signPlayers.put(player, uuid);
        return false;
    }
}
