package me.usefulorb.seedprotector;

import me.usefulorb.seedprotector.command.SeedProtectCommand;
import me.usefulorb.seedprotector.listener.PlayerNotificationListener;
import me.usefulorb.seedprotector.model.ProtectionReport;
import me.usefulorb.seedprotector.service.SeedManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main bootstrap class for SeedProtector plugin.
 */
public final class SeedProtectorPlugin extends JavaPlugin {

    private SeedManager seedManager;

    @Override
    public void onEnable() {
        this.seedManager = new SeedManager(getServer().getWorldContainer(), getLogger());

        registerCommands();
        registerListeners();

        ProtectionReport report = seedManager.inspectProtection();
        displayStartupStatus(report);
    }

    @Override
    public void onDisable() {
        getLogger().info("SeedProtector disabled successfully.");
    }

    private void registerCommands() {
        SeedProtectCommand commandHandler = new SeedProtectCommand(seedManager);
        PluginCommand command = getCommand("seedprotect");
        if (command != null) {
            command.setExecutor(commandHandler);
            command.setTabCompleter(commandHandler);
        } else {
            getLogger().severe("Failed to bind command /seedprotect from plugin.yml!");
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new PlayerNotificationListener(this, seedManager),
                this
        );
    }

    private void displayStartupStatus(ProtectionReport report) {
        if (report.isFullyProtected()) {
            getLogger().info("✔ SeedProtector: All " + report.totalConfigured() + " structure seeds are secured.");
        } else {
            getLogger().warning("=================================================");
            getLogger().warning(" SeedProtector: Server is VULNERABLE to seed cracking!");
            getLogger().warning(" " + report.vulnerableCount() + " structure seeds are set to vanilla defaults.");
            getLogger().warning(" Run '/seedprotect randomize' to secure your world.");
            getLogger().warning("=================================================");
        }
    }

    public SeedManager getSeedManager() {
        return seedManager;
    }
}
