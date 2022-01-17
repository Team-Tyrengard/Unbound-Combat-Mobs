package com.tyrengard.unbound.combat.mobs;

import com.tyrengard.unbound.combat.combatant.MobCombatant;
import org.bukkit.entity.Mob;

public class UCMob extends MobCombatant {
    protected transient Mob mob;

    public UCMob(Mob mob) {
        super(mob.getUniqueId());
        this.mob = mob;
    }

    @Override
    public Mob getEntity() {
        return mob;
    }
}
