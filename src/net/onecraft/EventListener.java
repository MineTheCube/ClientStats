package net.onecraft;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
    
    private UserData userdata;

    public EventListener(UserData userdata) {
        this.userdata = userdata;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        int version = ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();
        userdata.add(player, version);
    }
    
}
