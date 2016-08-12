package fr.onecraft.clientstats.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeClientStats extends Plugin {

    @Override
    public void onEnable() {
        getLogger().severe(ChatColor.RED + "---------------------");
        getLogger().severe(ChatColor.RED + "This is NOT a Bungeecord plugin yet.");
        getLogger().severe(ChatColor.RED + "Please put ClientStats on your Spigot servers !");
        getLogger().severe(ChatColor.RED + "---------------------");
    }

}
