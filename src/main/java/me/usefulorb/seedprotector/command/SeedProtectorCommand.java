package me.usefulorb.seedprotector.command;

import me.usefulorb.seedprotector.model.ProtectionReport;
import me.usefulorb.seedprotector.service.SeedManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SeedProtectorCommand implements CommandExecutor, TabCompleter {

    private final SeedManager seedManager;

    public SeedProtectorCommand(SeedManager seedManager) {
        this.seedManager = seedManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            sendHelp(sender, label);
            return true;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "audit", "check", "status" -> {
                if (!sender.hasPermission("seedprotector.audit") && !sender.hasPermission("seedprotector.admin")) {
                    sender.sendMessage(Component.text("You do not have permission to execute this command.", NamedTextColor.RED));
                    return true;
                }
                executeAudit(sender);
                return true;
            }
            case "randomize", "shuffle", "scramble" -> {
                if (!sender.hasPermission("seedprotector.randomize") && !sender.hasPermission("seedprotector.admin")) {
                    sender.sendMessage(Component.text("You do not have permission to execute this command.", NamedTextColor.RED));
                    return true;
                }
                executeRandomize(sender);
                return true;
            }
            default -> {
                sender.sendMessage(Component.text("Unknown subcommand. Type /" + label + " help for commands.", NamedTextColor.RED));
                return true;
            }
        }
    }

    private void executeAudit(CommandSender sender) {
        ProtectionReport report = seedManager.inspectProtection();

        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("✦ SeedProtector Audit Report ✦", NamedTextColor.AQUA, TextDecoration.BOLD));
        sender.sendMessage(Component.text("────────────────────────────────────────", NamedTextColor.DARK_GRAY));

        if (report.vulnerableCount() == 0) {
            sender.sendMessage(Component.text("✔ Structure Seeds: ", NamedTextColor.GRAY)
                    .append(Component.text("PROTECTED", NamedTextColor.GREEN, TextDecoration.BOLD))
                    .append(Component.text(" (" + report.totalConfigured() + "/" + report.totalConfigured() + " randomized)", NamedTextColor.DARK_GREEN)));
        } else {
            sender.sendMessage(Component.text("✖ Structure Seeds: ", NamedTextColor.GRAY)
                    .append(Component.text("VULNERABLE", NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Component.text(" (" + report.vulnerableCount() + " default constants detected)", NamedTextColor.RED)));
        }

        if (report.paperWorldProtected()) {
            sender.sendMessage(Component.text("✔ Paper Feature Seeds: ", NamedTextColor.GRAY)
                    .append(Component.text("ENABLED", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(Component.text("✖ Paper Feature Seeds: ", NamedTextColor.GRAY)
                    .append(Component.text("NOT CONFIGURED", NamedTextColor.GOLD)));
        }

        if (report.vulnerableCount() > 0 && report.vulnerableCount() <= 6) {
            sender.sendMessage(Component.text("Unrandomized:", NamedTextColor.GOLD));
            for (String key : report.defaultKeys()) {
                sender.sendMessage(Component.text("  • " + key, NamedTextColor.RED));
            }
        }

        sender.sendMessage(Component.text("────────────────────────────────────────", NamedTextColor.DARK_GRAY));
        if (report.hasVulnerabilities()) {
            sender.sendMessage(Component.text("Tip: Run /seedprotector randomize to secure your server.", NamedTextColor.YELLOW));
        }
        sender.sendMessage(Component.empty());
    }

    private void executeRandomize(CommandSender sender) {
        sender.sendMessage(Component.text("Randomizing structure seeds in configuration...", NamedTextColor.YELLOW));

        try {
            int modified = seedManager.randomizeAllSeeds();
            sender.sendMessage(Component.empty());
            sender.sendMessage(Component.text("✔ Success! ", NamedTextColor.GREEN, TextDecoration.BOLD)
                    .append(Component.text("Randomized " + modified + " structure seeds.", NamedTextColor.GREEN)));
            sender.sendMessage(Component.empty());
            sender.sendMessage(Component.text("CRITICAL INSTRUCTIONS:", NamedTextColor.GOLD, TextDecoration.BOLD));
            sender.sendMessage(Component.text(" 1. Stop the Minecraft server.", NamedTextColor.WHITE));
            sender.sendMessage(Component.text(" 2. Delete world folders (world, world_nether, world_the_end).", NamedTextColor.WHITE));
            sender.sendMessage(Component.text(" 3. Restart the server.", NamedTextColor.WHITE));
            sender.sendMessage(Component.text("Seed changes only affect newly generated chunks!", NamedTextColor.RED, TextDecoration.ITALIC));
            sender.sendMessage(Component.empty());
        } catch (IOException ex) {
            sender.sendMessage(Component.text("Failed to randomize seeds: " + ex.getMessage(), NamedTextColor.RED));
        }
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("✦ SeedProtector v1.0 ✦", NamedTextColor.AQUA, TextDecoration.BOLD)
                .append(Component.text(" by UsefulOrb (usefulorb.me)", NamedTextColor.DARK_AQUA)));
        sender.sendMessage(Component.text("Prevents world seed cracking by randomizing structure seeds.", NamedTextColor.GRAY));
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("/" + label + " audit", NamedTextColor.YELLOW)
                .append(Component.text(" - Run a security audit on current seeds", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/" + label + " randomize", NamedTextColor.YELLOW)
                .append(Component.text(" - Scramble structure & feature seeds", NamedTextColor.GRAY)));
        sender.sendMessage(Component.empty());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            if (sender.hasPermission("seedprotector.audit") || sender.hasPermission("seedprotector.admin")) {
                options.add("audit");
                options.add("check");
                options.add("status");
            }
            if (sender.hasPermission("seedprotector.randomize") || sender.hasPermission("seedprotector.admin")) {
                options.add("randomize");
                options.add("shuffle");
                options.add("scramble");
            }
            options.add("help");

            String prefix = args[0].toLowerCase();
            return options.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
