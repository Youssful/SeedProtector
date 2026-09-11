package me.usefulorb.seedprotector.model;

import java.util.List;

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
