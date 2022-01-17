package com.tyrengard.unbound.combat.mobs.spawning;

public record SpawnStrategy(String name, long ticks) {
    public static final SpawnStrategy REPLACE = new SpawnStrategy("replace", -1);
    public static final SpawnStrategy FORCED = new SpawnStrategy("forced", UCMConfig.getForcedSpawnFrequency());
}
