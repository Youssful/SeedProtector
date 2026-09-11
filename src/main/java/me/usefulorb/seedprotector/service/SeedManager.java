package me.usefulorb.seedprotector.service;

import me.usefulorb.seedprotector.model.ProtectionReport;
import me.usefulorb.seedprotector.model.StructureSeed;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Service managing structure seed inspection and randomization.
 */
public class SeedManager {

    private final File worldContainer;
    private final Logger logger;
    private volatile boolean vulnerable = false;

    public SeedManager(File worldContainer, Logger logger) {
        this.worldContainer = worldContainer;
        this.logger = logger;
    }

    /**
     * Inspects configuration files to determine if any structure seeds match default values.
     */
    public ProtectionReport inspectProtection() {
        File spigotFile = new File(worldContainer, "spigot.yml");
        Map<String, StructureSeed> registry = StructureSeed.getRegistry();

        int totalFound = 0;
        int vulnerableCount = 0;
        List<String> defaultKeys = new ArrayList<>();

        if (!spigotFile.exists()) {
            this.vulnerable = true;
            return new ProtectionReport(registry.size(), registry.size(), false, new ArrayList<>(registry.keySet()));
        }

        try {
            List<String> lines = Files.readAllLines(spigotFile.toPath(), StandardCharsets.UTF_8);
            lineScan:
            for (String rawLine : lines) {
                String line = rawLine.trim();
                for (Map.Entry<String, StructureSeed> entry : registry.entrySet()) {
                    String key = entry.getKey();
                    if (line.startsWith(key + ":")) {
                        totalFound++;
                        String val = line.substring(key.length() + 1).trim();
                        if (val.equalsIgnoreCase(entry.getValue().getDefaultConstant())) {
                            vulnerableCount++;
                            defaultKeys.add(entry.getValue().getLabel() + " (" + key + ")");
                        }
                        continue lineScan;
                    }
                }
            }
        } catch (IOException ex) {
            logger.warning("Failed reading spigot.yml during seed audit: " + ex.getMessage());
        }

        boolean paperConfigProtected = false;
        File paperConfigFile = new File(worldContainer, "config/paper-world-defaults.yml");
        if (paperConfigFile.exists()) {
            try {
                for (String rawLine : Files.readAllLines(paperConfigFile.toPath(), StandardCharsets.UTF_8)) {
                    if ("generate-random-seeds-for-all: true".equalsIgnoreCase(rawLine.trim())) {
                        paperConfigProtected = true;
                        break;
                    }
                }
            } catch (IOException ignored) {
            }
        }

        this.vulnerable = (vulnerableCount > 0) || !paperConfigProtected;
        return new ProtectionReport(totalFound, vulnerableCount, paperConfigProtected, defaultKeys);
    }

    /**
     * Scrambles structure seeds in spigot.yml and enables Paper world seed randomization.
     *
     * @return count of structure seeds randomized, or -1 if an error occurred.
     */
    public int randomizeAllSeeds() throws IOException {
        File spigotFile = new File(worldContainer, "spigot.yml");
        if (!spigotFile.exists()) {
            throw new IOException("spigot.yml not found in server root.");
        }

        Map<String, StructureSeed> registry = StructureSeed.getRegistry();
        List<String> lines = Files.readAllLines(spigotFile.toPath(), StandardCharsets.UTF_8);
        List<String> updated = new ArrayList<>(lines.size());
        int modifiedCount = 0;

        for (String originalLine : lines) {
            String trimmed = originalLine.trim();
            boolean matched = false;

            for (String key : registry.keySet()) {
                if (trimmed.startsWith(key + ":")) {
                    long secureSeed = generateCrypticSeed();
                    String leadingWhitespace = originalLine.substring(0, originalLine.indexOf(key));
                    updated.add(leadingWhitespace + key + ": " + secureSeed);
                    matched = true;
                    modifiedCount++;
                    break;
                }
            }

            if (!matched) {
                updated.add(originalLine);
            }
        }

        Files.write(spigotFile.toPath(), updated, StandardCharsets.UTF_8);

        // Update Paper configuration if present
        File paperConfig = new File(worldContainer, "config/paper-world-defaults.yml");
        if (paperConfig.exists()) {
            try {
                List<String> paperLines = Files.readAllLines(paperConfig.toPath(), StandardCharsets.UTF_8);
                List<String> paperUpdated = new ArrayList<>(paperLines.size());
                boolean foundFlag = false;

                for (String pLine : paperLines) {
                    if (pLine.trim().startsWith("generate-random-seeds-for-all:")) {
                        String indent = pLine.substring(0, pLine.indexOf("generate-random-seeds-for-all"));
                        paperUpdated.add(indent + "generate-random-seeds-for-all: true");
                        foundFlag = true;
                        continue;
                    }
                    paperUpdated.add(pLine);
                }

                if (foundFlag) {
                    Files.write(paperConfig.toPath(), paperUpdated, StandardCharsets.UTF_8);
                }
            } catch (IOException ex) {
                logger.warning("Could not patch paper-world-defaults.yml: " + ex.getMessage());
            }
        }

        this.vulnerable = false;
        return modifiedCount;
    }

    /**
     * Generates a 9-digit pseudo-random seed between 100,000,000 and 999,999,999.
     */
    private long generateCrypticSeed() {
        return ThreadLocalRandom.current().nextLong(100_000_000L, 1_000_000_000L);
    }

    public boolean isVulnerable() {
        return vulnerable;
    }
}
