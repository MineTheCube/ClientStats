package fr.onecraft.clientstats;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    protected static final String PREFIX = "§9[ClientStats] §f";

    protected int totalJoined;

    protected HashMap<UUID, String> joinedPlayers = new HashMap<UUID, String>();

    @Override
    public void onEnable() {

        // Save Default Config
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        // Handle command
        getCommand("clientstats").setExecutor(new CommandHandler(this));

        // Register Event
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        // Plugin Loaded !
        getLogger().info("ClientStats v" + getDescription().getVersion() + " loaded !");

    }

    @Override
    public void onDisable() {
    }

    public void sendMessage(CommandSender sender, String messageCode, String... args) {
        String message = this.getConfig().getString("messages." + messageCode);

        if (message == null) {

            getLogger().warning("Missing message: " + message);
            message = this.getConfig().getString("messages.error.general");

            if (message == null) {

                message = "&cAn internal error occurred..";

            }

        }

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + (i+1) + "}", args[i]);
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.PREFIX + message));

    }

    public String getVersion(Player player) {

        try {

            Method getHandle = player.getClass().getMethod("getHandle");
            Object nmsPlayer = getHandle.invoke(player);

            Field fieldPlayerConnection = nmsPlayer.getClass().getField("playerConnection");
            Object playerConnection = fieldPlayerConnection.get(nmsPlayer);

            Field fieldNetworkManager = playerConnection.getClass().getField("networkManager");
            Object networkManager = fieldNetworkManager.get(playerConnection);

            Method getVersion = networkManager.getClass().getMethod("getVersion");
            Object value = getVersion.invoke(networkManager);
            Integer version = (Integer) value;

            String versionName = this.getConfig().getString("versions." + version.toString());

            if (versionName == null) {

                versionName = this.getConfig().getString("versions.0");

                if (versionName == null) {

                    getLogger().warning("Missing message: versions.0");
                    return null;

                }

            }

            return versionName;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

}
