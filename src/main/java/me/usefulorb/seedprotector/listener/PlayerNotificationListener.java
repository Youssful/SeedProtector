package me.usefulorb.seedprotector.listener;

import me.usefulorb.seedprotector.service.SeedManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

/**
 * Listens for administrative joins and notifies them if seed protection is missing.
 */
public class PlayerNotificationListener implements Listener {

    private final Plugin plugin;
    private final SeedManager seedManager;

    public PlayerNotificationListener(Plugin plugin, SeedManager seedManager) {
        this.plugin = plugin;
        this.seedManager = seedManager;
    }

    @EventHandler
    public void onAdminJoin(PlayerJoinEvent event) {
        if (!seedManager.isVulnerable()) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("seedprotect.admin")
                && !player.hasPermission("seedprotect.randomize")
                && !player.hasPermission("seedprotector.scramble")) {
            return;
        }

        // Delay delivery by 40 ticks (2 seconds) so client finishes loading screen
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }

            player.sendMessage(Component.empty());
            player.sendMessage(Component.text("⚠ SeedProtector Security Warning ⚠", NamedTextColor.RED, TextDecoration.BOLD));
            player.sendMessage(Component.text("Default structure seeds were detected on this server.", NamedTextColor.RED));
            player.sendMessage(Component.text("Your world is vulnerable to seed cracking exploits!", NamedTextColor.GRAY));
            player.sendMessage(Component.text("Run ", NamedTextColor.YELLOW)
                    .append(Component.text("/seedprotect randomize", NamedTextColor.WHITE, TextDecoration.UNDERLINED))
                    .append(Component.text(" to secure your server.", NamedTextColor.YELLOW)));
            player.sendMessage(Component.empty());
        }, 40L);
    }
}
