package com.tyrengard.unbound.combat.mobs.spawning;

import com.tyrengard.unbound.combat.mobs.MobTemplate;

public interface SpawnCondition {
    boolean passCheck(MobTemplate templateToSpawn);
}
