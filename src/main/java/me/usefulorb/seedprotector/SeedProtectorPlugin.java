package me.usefulorb.seedprotector;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class SeedProtectorPlugin extends JavaPlugin implements CommandExecutor, TabCompleter, Listener {

    private static final Map<String, String> DEFAULT_SEEDS = new LinkedHashMap<>();
    private boolean serverVulnerable = false;

    static {
        DEFAULT_SEEDS.put("seed-village", "10387312");
        DEFAULT_SEEDS.put("seed-desert", "14357617");
        DEFAULT_SEEDS.put("seed-igloo", "14357618");
        DEFAULT_SEEDS.put("seed-jungle", "14357619");
        DEFAULT_SEEDS.put("seed-swamp", "14357620");
        DEFAULT_SEEDS.put("seed-monument", "10387313");
        DEFAULT_SEEDS.put("seed-shipwreck", "165745295");
        DEFAULT_SEEDS.put("seed-ocean", "14357621");
        DEFAULT_SEEDS.put("seed-outpost", "165745296");
        DEFAULT_SEEDS.put("seed-endcity", "10387313");
        DEFAULT_SEEDS.put("seed-slime", "987234911");
        DEFAULT_SEEDS.put("seed-nether", "30084232");
        DEFAULT_SEEDS.put("seed-mansion", "10387319");
        DEFAULT_SEEDS.put("seed-fossil", "14357921");
        DEFAULT_SEEDS.put("seed-portal", "34222645");
        DEFAULT_SEEDS.put("seed-ancientcity", "20083232");
        DEFAULT_SEEDS.put("seed-trailruins", "83469867");
        DEFAULT_SEEDS.put("seed-trialchambers", "94251327");
        DEFAULT_SEEDS.put("seed-buriedtreasure", "10387320");
        DEFAULT_SEEDS.put("seed-mineshaft", "default");
        DEFAULT_SEEDS.put("seed-stronghold", "default");
    }

    @Override
    public void onEnable() {
        PluginCommand command = getCommand("seedprotector");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
        getServer().getPluginManager().registerEvents(this, this);

        StatusResult status = checkStatus();
        this.serverVulnerable = status.vulnerable > 0;
        if (this.serverVulnerable) {
            getLogger().warning("===========================================");
            getLogger().warning(" SeedProtector: " + status.vulnerable + " structure seed(s) are still DEFAULT!");
            getLogger().warning(" Your server is VULNERABLE to seed cracking.");
            getLogger().warning(" Run /seedprotector scramble to protect your server.");
            getLogger().warning("===========================================");
        } else {
            getLogger().info("SeedProtector: All " + status.total + " structure seeds are randomized. Server is protected!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "SeedProtector v1.0");
            sender.sendMessage(ChatColor.GRAY + "Prevents seed cracking by randomizing structure seeds.");
            sender.sendMessage(ChatColor.GRAY + "Author: " + ChatColor.WHITE + "UsefulOrb " + ChatColor.DARK_GRAY + "(usefulorb.me)");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + "/seedprotector status" + ChatColor.GRAY + " - Check protection status");
            sender.sendMessage(ChatColor.YELLOW + "/seedprotector scramble" + ChatColor.GRAY + " - Randomize all structure seeds");
            return true;
        }

        String sub = args[0].toLowerCase();
        if ("status".equals(sub)) {
            if (!sender.hasPermission("seedprotector.status") && !sender.hasPermission("seedshield.status")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            return handleStatus(sender);
        }

        if ("scramble".equals(sub)) {
            if (!sender.hasPermission("seedprotector.scramble") && !sender.hasPermission("seedshield.scramble")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }
            return handleScramble(sender);
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /seedprotector status or /seedprotector scramble");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("seedprotector.status") || sender.hasPermission("seedshield.status")) {
                completions.add("status");
            }
            if (sender.hasPermission("seedprotector.scramble") || sender.hasPermission("seedshield.scramble")) {
                completions.add("scramble");
            }
            String query = args[0].toLowerCase();
            return completions.stream()
                    .filter(s -> s.startsWith(query))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!this.serverVulnerable) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.hasPermission("seedprotector.scramble") && !player.hasPermission("seedshield.scramble")) {
            return;
        }

        getServer().getScheduler().runTaskLater(this, () -> {
            if (!player.isOnline()) {
                return;
            }
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " SeedProtector Warning");
            player.sendMessage(ChatColor.RED + " Your server is " + ChatColor.BOLD + "VULNERABLE" + ChatColor.RED + " to seed cracking!");
            player.sendMessage(ChatColor.RED + " Structure seeds are still set to default values.");
            player.sendMessage(ChatColor.YELLOW + " Run " + ChatColor.WHITE + "/seedprotector scramble" + ChatColor.YELLOW + " to protect your server.");
            player.sendMessage("");
        }, 40L);
    }

    private boolean handleStatus(CommandSender sender) {
        StatusResult status = checkStatus();
        sender.sendMessage("");
        sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + " SeedProtector Status");
        sender.sendMessage(ChatColor.GRAY + " -------------------------");
        if (status.vulnerable == 0) {
            sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " PROTECTED");
            sender.sendMessage(ChatColor.GRAY + " All " + status.total + " structure seeds are randomized.");
        } else {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " VULNERABLE");
            sender.sendMessage(ChatColor.RED + " " + status.vulnerable + "/" + status.total + " seeds are still DEFAULT.");
            sender.sendMessage(ChatColor.YELLOW + " Run " + ChatColor.WHITE + "/seedprotector scramble" + ChatColor.YELLOW + " to fix.");
        }

        if (status.paperProtected) {
            sender.sendMessage(ChatColor.GREEN + " Paper feature seeds: randomized");
        } else {
            sender.sendMessage(ChatColor.YELLOW + " Paper feature seeds: not enabled");
        }
        sender.sendMessage(ChatColor.GRAY + " -------------------------");

        if (status.vulnerable > 0 && status.vulnerable <= 5) {
            sender.sendMessage(ChatColor.RED + " Default seeds:");
            for (String key : status.defaultKeys) {
                sender.sendMessage(ChatColor.RED + "  - " + key);
            }
        }
        sender.sendMessage("");
        return true;
    }

    private boolean handleScramble(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Scrambling structure seeds...");
        File spigotFile = new File(getServer().getWorldContainer(), "spigot.yml");
        if (!spigotFile.exists()) {
            sender.sendMessage(ChatColor.RED + "Could not find spigot.yml!");
            return true;
        }

        try {
            List<String> lines = Files.readAllLines(spigotFile.toPath(), StandardCharsets.UTF_8);
            List<String> modified = new ArrayList<>();
            int changed = 0;
            for (String line : lines) {
                String trimmed = line.trim();
                boolean replaced = false;
                for (String seedKey : DEFAULT_SEEDS.keySet()) {
                    if (trimmed.startsWith(seedKey + ":")) {
                        int randomSeed = ThreadLocalRandom.current().nextInt(10000000, 99999999);
                        String indent = line.substring(0, line.indexOf(seedKey));
                        modified.add(indent + seedKey + ": " + randomSeed);
                        replaced = true;
                        changed++;
                        break;
                    }
                }
                if (!replaced) {
                    modified.add(line);
                }
            }
            Files.write(spigotFile.toPath(), modified, StandardCharsets.UTF_8);
            sender.sendMessage(ChatColor.GREEN + "  Randomized " + changed + " structure seeds in spigot.yml");
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "  Failed to modify spigot.yml: " + e.getMessage());
            getLogger().warning("Failed to modify spigot.yml: " + e.getMessage());
            return true;
        }

        File paperConfig = new File(getServer().getWorldContainer(), "config/paper-world-defaults.yml");
        if (paperConfig.exists()) {
            try {
                List<String> lines = Files.readAllLines(paperConfig.toPath(), StandardCharsets.UTF_8);
                List<String> modified = new ArrayList<>();
                boolean changed = false;
                for (String line : lines) {
                    if (line.trim().startsWith("generate-random-seeds-for-all:")) {
                        String indent = line.substring(0, line.indexOf("generate-random-seeds-for-all"));
                        modified.add(indent + "generate-random-seeds-for-all: true");
                        changed = true;
                        continue;
                    }
                    modified.add(line);
                }
                if (changed) {
                    Files.write(paperConfig.toPath(), modified, StandardCharsets.UTF_8);
                    sender.sendMessage(ChatColor.GREEN + "  Enabled generate-random-seeds-for-all in Paper config");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "  Could not find generate-random-seeds-for-all setting");
                }
            } catch (IOException e) {
                sender.sendMessage(ChatColor.YELLOW + "  Could not modify Paper config: " + e.getMessage());
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "  - Paper config not found (only needed for Paper servers)");
        }

        this.serverVulnerable = false;
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " Seeds scrambled successfully!");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + " IMPORTANT:");
        sender.sendMessage(ChatColor.WHITE + "  1. " + ChatColor.GRAY + "Stop the server");
        sender.sendMessage(ChatColor.WHITE + "  2. " + ChatColor.GRAY + "Delete all world folders (world, world_nether, world_the_end)");
        sender.sendMessage(ChatColor.WHITE + "  3. " + ChatColor.GRAY + "Start the server again");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RED + "  Seeds only take effect on newly generated worlds!");
        sender.sendMessage("");
        return true;
    }

    private StatusResult checkStatus() {
        StatusResult result = new StatusResult();
        File spigotFile = new File(getServer().getWorldContainer(), "spigot.yml");
        if (!spigotFile.exists()) {
            result.vulnerable = DEFAULT_SEEDS.size();
            result.total = DEFAULT_SEEDS.size();
            return result;
        }

        try {
            List<String> lines = Files.readAllLines(spigotFile.toPath(), StandardCharsets.UTF_8);
            lineLoop:
            for (String line : lines) {
                String trimmed = line.trim();
                for (Map.Entry<String, String> entry : DEFAULT_SEEDS.entrySet()) {
                    if (trimmed.startsWith(entry.getKey() + ":")) {
                        result.total++;
                        String value = trimmed.substring(trimmed.indexOf(':') + 1).trim();
                        if (value.equals(entry.getValue())) {
                            result.vulnerable++;
                            result.defaultKeys.add(entry.getKey());
                        }
                        continue lineLoop;
                    }
                }
            }
        } catch (IOException e) {
            getLogger().warning("Could not read spigot.yml: " + e.getMessage());
        }

        File paperConfig = new File(getServer().getWorldContainer(), "config/paper-world-defaults.yml");
        if (paperConfig.exists()) {
            try {
                for (String line : Files.readAllLines(paperConfig.toPath(), StandardCharsets.UTF_8)) {
                    if ("generate-random-seeds-for-all: true".equals(line.trim())) {
                        result.paperProtected = true;
                        break;
                    }
                }
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    private static class StatusResult {
        int vulnerable = 0;
        int total = 0;
        boolean paperProtected = false;
        final List<String> defaultKeys = new ArrayList<>();
    }
}
