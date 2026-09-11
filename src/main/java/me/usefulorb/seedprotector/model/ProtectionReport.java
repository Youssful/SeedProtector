package me.usefulorb.seedprotector.model;

import java.util.List;

/**
 * Immutable report representing the seed protection status of the server.
 *
 * @param totalConfigured    Total structures tracked in configuration.
 * @param vulnerableCount    Number of structures still using known default seeds.
 * @param paperWorldProtected Whether Paper's generate-random-seeds-for-all is enabled.
 * @param defaultKeys        List of structure keys that are still vulnerable.
 */
public record ProtectionReport(
        int totalConfigured,
        int vulnerableCount,
        boolean paperWorldProtected,
        List<String> defaultKeys
) {
    public boolean isFullyProtected() {
        return vulnerableCount == 0 && paperWorldProtected;
    }

    public boolean hasVulnerabilities() {
        return vulnerableCount > 0 || !paperWorldProtected;
    }
}
