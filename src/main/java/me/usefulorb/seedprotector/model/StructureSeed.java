package me.usefulorb.seedprotector.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public enum StructureSeed {
    VILLAGE("seed-village", "10387312", "Village"),
    DESERT_PYRAMID("seed-desert", "14357617", "Desert Pyramid"),
    IGLOO("seed-igloo", "14357618", "Igloo"),
    JUNGLE_TEMPLE("seed-jungle", "14357619", "Jungle Temple"),
    SWAMP_HUT("seed-swamp", "14357620", "Swamp Hut"),
    OCEAN_MONUMENT("seed-monument", "10387313", "Ocean Monument"),
    SHIPWRECK("seed-shipwreck", "165745295", "Shipwreck"),
    OCEAN_RUIN("seed-ocean", "14357621", "Ocean Ruin"),
    PILLAGER_OUTPOST("seed-outpost", "165745296", "Pillager Outpost"),
    END_CITY("seed-endcity", "10387313", "End City"),
    SLIME_CHUNK("seed-slime", "987234911", "Slime Chunk"),
    NETHER_COMPLEX("seed-nether", "30084232", "Nether Complex"),
    WOODLAND_MANSION("seed-mansion", "10387319", "Woodland Mansion"),
    FOSSIL("seed-fossil", "14357921", "Fossil"),
    RUINED_PORTAL("seed-portal", "34222645", "Ruined Portal"),
    ANCIENT_CITY("seed-ancientcity", "20083232", "Ancient City"),
    TRAIL_RUINS("seed-trailruins", "83469867", "Trail Ruins"),
    TRIAL_CHAMBERS("seed-trialchambers", "94251327", "Trial Chambers"),
    BURIED_TREASURE("seed-buriedtreasure", "10387320", "Buried Treasure"),
    MINESHAFT("seed-mineshaft", "default", "Mineshaft"),
    STRONGHOLD("seed-stronghold", "default", "Stronghold");

    private final String configKey;
    private final String defaultConstant;
    private final String label;

    private static final Map<String, StructureSeed> BY_KEY;

    static {
        Map<String, StructureSeed> map = new LinkedHashMap<>();
        for (StructureSeed structure : values()) {
            map.put(structure.configKey, structure);
        }
        BY_KEY = Collections.unmodifiableMap(map);
    }

    StructureSeed(String configKey, String defaultConstant, String label) {
        this.configKey = configKey;
        this.defaultConstant = defaultConstant;
        this.label = label;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getDefaultConstant() {
        return defaultConstant;
    }

    public String getLabel() {
        return label;
    }

    public static Map<String, StructureSeed> getRegistry() {
        return BY_KEY;
    }
}
