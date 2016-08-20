package net.onecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class UserData {

    public static final int PROTOCOL_1_8_0 = 47;
    public static final int PROTOCOL_1_7_6 = 5;
    public static final int PROTOCOL_1_7_2 = 4;
    public static final int PROTOCOL_UNKNOWN = -1;

    private int version_1_8_0 = 0;
    private int version_1_7_6 = 0;
    private int version_1_7_2 = 0;
    private int version_unknown = 0;
    
    private ArrayList<UUID> uniqueUsers = new ArrayList<UUID>();
    
    public void add(Player player, int version) {
        UUID uuid = player.getUniqueId();
        if (!uniqueUsers.contains(uuid)) {
            switch (version) {
                case UserData.PROTOCOL_1_8_0:
                    version_1_8_0++;
                    break;
    
                case UserData.PROTOCOL_1_7_6:
                    version_1_7_6++;
                    break;
    
                case UserData.PROTOCOL_1_7_2:
                    version_1_7_2++;
                    break;

                default:
                    version_unknown++;
                    break;
            }
            uniqueUsers.add(uuid);
        }
    }
    
    public HashMap<Integer,Integer> getStats() {
        HashMap<Integer,Integer> map = new HashMap<Integer, Integer>();
        map.put(UserData.PROTOCOL_1_7_2, version_1_7_2);
        map.put(UserData.PROTOCOL_1_7_6, version_1_7_6);
        map.put(UserData.PROTOCOL_1_8_0, version_1_8_0);
        map.put(UserData.PROTOCOL_UNKNOWN, version_unknown);
        return map;
    }
    
    public void delete() {
        uniqueUsers = null;
    }

}
