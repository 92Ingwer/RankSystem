package org.kim.rankSystem;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kim.rankSystem.commands.GroupCommand;
import org.kim.rankSystem.commands.SetSignCommand;
import org.kim.rankSystem.database.SQL;
import org.kim.rankSystem.database.SQLCreate;
import org.kim.rankSystem.enums.MessageEnum;
import org.kim.rankSystem.gui.EditGroupGUI;
import org.kim.rankSystem.gui.EditGroupPermissionsGUI;
import org.kim.rankSystem.gui.EditPlayerPermissionsGUI;
import org.kim.rankSystem.listeners.ChatRendererListener;
import org.kim.rankSystem.listeners.InteractWithSignListener;
import org.kim.rankSystem.listeners.OnJoinListener;
import org.kim.rankSystem.listeners.OnQuitListener;
import org.kim.rankSystem.scoreboard.Scoreboards;
import org.kim.rankSystem.services.GroupService;

public final class RankSystem extends JavaPlugin {
    @Getter
    public static RankSystem instance;
    @Getter
    public static SQL sql;
    @Override

    public void onEnable() {
        instance = this;
        FileConfiguration config = getConfig();
        initdb();
        setRanks();
        startScheduler();
        saveDefaultConfig();
        MessageEnum.loadPrefixes(config);
        Bukkit.getPluginManager().registerEvents(new OnJoinListener(),this);
        Bukkit.getPluginManager().registerEvents(new OnQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatRendererListener(), this);
        Bukkit.getPluginManager().registerEvents(new EditGroupGUI(), this);
        Bukkit.getPluginManager().registerEvents(new EditGroupPermissionsGUI(), this);
        Bukkit.getPluginManager().registerEvents(new EditPlayerPermissionsGUI(), this);
        Bukkit.getPluginManager().registerEvents(new InteractWithSignListener(), this);
        this.getCommand("group").setExecutor(new GroupCommand());
        this.getCommand("setsign").setExecutor(new SetSignCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void initdb() {
        sql = new SQL("", "", "", "", "");
        sql.update("USE ");
        SQLCreate.create();
    }

    public void setRanks() {
        Bukkit.getScheduler().runTaskAsynchronously(instance, GroupService::setAllRanks);
    }
    public void startScheduler() {
        Scoreboards.updateScoreboard();
        GroupService.checkGroupScheduler();
    }
}
