package fr.onecraft.clientstats.bukkit.hooks;

import fr.onecraft.core.event.Events;
import fr.onecraft.core.task.Tasks;
import fr.onecraft.core.plugin.Core;
import fr.onecraft.core.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPacketHandler extends AbstractProvider implements Listener {

    // Protocol map
    private final Map<InetSocketAddress, Pair<Integer, Long>> versions = new ConcurrentHashMap<>();

    // Timeout
    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(40);

    public AbstractPacketHandler() {

        super();

        Events.register(this);

        registerPacketListener();

        Core.warning("The use of " + getProviderName() + " is experimental, it may not be stable and efficient for large servers.");

        Tasks.after(1, TimeUnit.MINUTES).every(1, TimeUnit.MINUTES).async(new BukkitRunnable() {
            @Override
            public void run() {

                // In addresses we have all the current connected addresses
                Set<InetSocketAddress> addresses = new HashSet<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    addresses.add(player.getAddress());
                }

                Iterator<Map.Entry<InetSocketAddress, Pair<Integer, Long>>> it = versions.entrySet().iterator();
                long now = System.currentTimeMillis();

                while (it.hasNext()) {
                    Map.Entry<InetSocketAddress, Pair<Integer, Long>> entry = it.next();
                    // Address tried to connect, but timed out (still not logged in)
                    if (entry.getValue().getRight() + TIMEOUT < now && !addresses.contains(entry.getKey())) {
                        it.remove();
                    }
                }
            }
        });

    }

    protected void add(InetSocketAddress address, int protocolVersion) {
        versions.put(address, Pair.of(protocolVersion, System.currentTimeMillis()));
    }

    protected void remove(InetSocketAddress address) {
        versions.remove(address);
    }

    @Override
    public int getProtocol(Player p) {
        Pair<Integer, Long> pair = versions.get(p.getAddress());
        return pair == null ? 0 : pair.getLeft();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Player decided to quit the server
        versions.remove(event.getPlayer().getAddress());
    }

    public abstract void registerPacketListener();

}
