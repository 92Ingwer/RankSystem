package org.kim.SkyGens.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

public class SkullBuilder {
    public static ItemStack getCustomSkull(String url, String name, String... lore) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty()) return head;

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        skullMeta.setDisplayName(name);
        skullMeta.setLore(Arrays.asList(lore));
        head.setItemMeta(skullMeta);
        return head;
    }
    public static ItemStack getPlayerHead(String playerName, String name, String... lore) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if (skullMeta != null) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
            skullMeta.setDisplayName(name);
            skullMeta.setLore(Arrays.asList(lore));
            playerHead.setItemMeta(skullMeta);
        }
        return playerHead;
    }

}
