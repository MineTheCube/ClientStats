package net.onecraft;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.onecraft.UserData;

import org.bukkit.plugin.java.JavaPlugin;

public class ClientStats extends JavaPlugin {
    
    private UserData userdata;
    
    @Override
    public void onEnable() {
        getLogger().info("Loading ClientStats ...");

        // Create UserData
        userdata = new UserData();
        
        // Register command
        getCommand("clientstats").setExecutor(new CommandHandler(userdata));
        
        // Register event
        getServer().getPluginManager().registerEvents(new EventListener(userdata), this);

        getLogger().info("ClientStats loaded !");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ClientStats v1 disabled.");
        log();
        userdata.delete();
    }
    
    public void log()  {
        try {
            File dataFolder = getDataFolder();
            if(!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String filename = dateFormat.format(new Date()) + ".log";
            File saveTo = new File(dataFolder, filename);
            if(!saveTo.exists()) {
                saveTo.createNewFile();
            }
            FileWriter fw = new FileWriter(saveTo, false);
            PrintWriter pw = new PrintWriter(fw);

            HashMap<Integer,Integer> data = userdata.getStats();
            int v_1_8_0 = data.get(UserData.PROTOCOL_1_8_0);
            int v_1_7_6 = data.get(UserData.PROTOCOL_1_7_6);
            int v_1_7_2 = data.get(UserData.PROTOCOL_1_7_2);
            int v_unknown = data.get(UserData.PROTOCOL_UNKNOWN);
            int total = v_1_8_0 + v_1_7_6 + v_1_7_2 + v_unknown;
            
            pw.println("Version des joueurs:");
            pw.println("Total:            " + total);
            
            if (total == 0) total = 1;
            double P_1_8_0 = v_1_8_0 * 100.0 / total;
            double P_1_7_6 = v_1_7_6 * 100.0 / total;
            double P_1_7_2 = v_1_7_2 * 100.0 / total;
            double P_unknown = v_unknown * 100.0 / total;

            pw.println("1.8.0 - 1.8.3:    " + v_1_8_0 + " (" + Math.round(P_1_8_0) + "%)");
            pw.println("1.7.6 - 1.7.9:    " + v_1_7_6 + " (" + Math.round(P_1_7_6) + "%)");
            pw.println("1.7.2 - 1.7.5:    " + v_1_7_2 + " (" + Math.round(P_1_7_2) + "%)");
            pw.println("Inconnu:          " + v_unknown + " (" + Math.round(P_unknown) + "%)");
            
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
