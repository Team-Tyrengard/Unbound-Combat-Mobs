package com.tyrengard.unbound.combat.mobs.spawning;

import com.tyrengard.unbound.combat.mobs.MobManager;
import com.tyrengard.unbound.combat.mobs.MobTemplate;

public record MobCountCondition(int max) implements SpawnCondition {
    @Override
    public boolean passCheck(MobTemplate templateToSpawn) {
        return MobManager.getMobCount(templateToSpawn) < max;
    }
}
