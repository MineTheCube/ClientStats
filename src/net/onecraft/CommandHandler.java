package net.onecraft;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    public UserData userdata;
    
    public CommandHandler(UserData userdata) {
        this.userdata = userdata;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("show")) {

                HashMap<Integer,Integer> data = userdata.getStats();
                int v_1_8_0 = data.get(UserData.PROTOCOL_1_8_0);
                int v_1_7_6 = data.get(UserData.PROTOCOL_1_7_6);
                int v_1_7_2 = data.get(UserData.PROTOCOL_1_7_2);
                int unknown = data.get(UserData.PROTOCOL_UNKNOWN);
                sendStats(sender, v_1_8_0, v_1_7_6, v_1_7_2, unknown);
                return true;
                
            } else if (args[0].equalsIgnoreCase("online")) {

                int v_1_8_0 = 0;
                int v_1_7_6 = 0;
                int v_1_7_2 = 0;
                int unknown = 0;
                for (Player player: Bukkit.getServer().getOnlinePlayers()) {
                    int version = ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();
                    switch (version) {
                        case UserData.PROTOCOL_1_8_0:
                            v_1_8_0++;
                            break;
            
                        case UserData.PROTOCOL_1_7_6:
                            v_1_7_6++;
                            break;
            
                        case UserData.PROTOCOL_1_7_2:
                            v_1_7_2++;
                            break;
    
                        default:
                            unknown++;
                            break;
                    }
                }
                sendStats(sender, v_1_8_0, v_1_7_6, v_1_7_2, unknown);
                return true;
                
            } else if (args[0].equalsIgnoreCase("reload")) {
                
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "pluginmanager reload ClientStats");
                sender.sendMessage("§9[Stats] §fPlugin rechargé !");
                return true;
                
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("player")) {
                
                Player target = Bukkit.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(args[1] + " n'est pas connecté.");
                } else {
                    int version = ((CraftPlayer) target).getHandle().playerConnection.networkManager.getVersion();
                    switch (version) {
                        case UserData.PROTOCOL_1_8_0:
                            sender.sendMessage("§9[Stats] §f" + target.getName() + " est en version 1.8.0-1.8.3");
                            break;
            
                        case UserData.PROTOCOL_1_7_6:
                            sender.sendMessage("§9[Stats] §f" + target.getName() + " est en version 1.7.6-1.7.9");
                            break;
            
                        case UserData.PROTOCOL_1_7_2:
                            sender.sendMessage("§9[Stats] §f" + target.getName() + " est en version 1.7.2-1.7.5");
                            break;
    
                        default:
                            sender.sendMessage("§9[Stats] §f" + target.getName() + " utilise un protocole inconnu: "+version);
                            break;
                    }
                }
                return true;
                
            }
        }
        
        sender.sendMessage("§7Aide: ClientStats");
        sender.sendMessage("§b/clientstats show §7- Version des joueurs ayant rejoins");
        sender.sendMessage("§b/clientstats online §7- Version des joueurs en ligne");
        sender.sendMessage("§b/clientstats player <player> §7- Version du joueur");
        sender.sendMessage("§b/clientstats reload §7- Recharger le plugin");
        return true;
    }
    
    public void sendStats(CommandSender p, int v_1_8_0, int v_1_7_6, int v_1_7_2, int v_unknown) {
        int total = v_1_8_0 + v_1_7_6 + v_1_7_2 + v_unknown;
        
        p.sendMessage("§9[Stats] §fVersion des joueurs:");
        p.sendMessage("Total:            " + ChatColor.BOLD + total);
        
        if (total == 0) total = 1;
        double P_1_8_0 = v_1_8_0 * 100.0 / total;
        double P_1_7_6 = v_1_7_6 * 100.0 / total;
        double P_1_7_2 = v_1_7_2 * 100.0 / total;
        double P_unknown = v_unknown * 100.0 / total;
        
        p.sendMessage("1.8.0 - 1.8.3:    " + v_1_8_0 + " §7(" + Math.round(P_1_8_0) + "%)");
        p.sendMessage("1.7.6 - 1.7.9:    " + v_1_7_6 + " §7(" + Math.round(P_1_7_6) + "%)");
        p.sendMessage("1.7.2 - 1.7.5:    " + v_1_7_2 + " §7(" + Math.round(P_1_7_2) + "%)");
        p.sendMessage("Inconnu:    §0.§f    " + v_unknown + " §7(" + Math.round(P_unknown) + "%)");
    }

}
