package fr.onecraft.clientstats;

/**
 * ClientStatsAPI holder
 * <p>
 * It's independent from both Bukkit and Bungeecord
 */
public class ClientStats {

    public static final String EXEMPT_PERMISSION = "clientstats.exempt";

    private static ClientStatsAPI api;

    public static ClientStatsAPI getApi() {
        return api;
    }

    public static void setApi(ClientStatsAPI api) {
        ClientStats.api = api;
    }

}
