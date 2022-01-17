package com.tyrengard.unbound.combat.mobs.spawning;

import com.tyrengard.unbound.combat.mobs.MobTemplate;

public record SpawnRateCondition(double rate) implements SpawnCondition {
    @Override
    public boolean passCheck(MobTemplate templateToSpawn) {
        return Math.random() <= rate;
    }
}
