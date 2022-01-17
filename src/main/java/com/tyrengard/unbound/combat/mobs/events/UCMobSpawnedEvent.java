package com.tyrengard.unbound.combat.mobs.events;

import com.tyrengard.unbound.combat.mobs.MobTemplate;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class UCMobSpawnedEvent extends Event {
    // region Base event components
    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
    // endregion

    private final MobTemplate mobTemplate;
    private final Mob mob;

    public UCMobSpawnedEvent(MobTemplate mobTemplate, Mob mob) {
        this.mobTemplate = mobTemplate;
        this.mob = mob;
    }

    public MobTemplate getMobTemplate() {
        return mobTemplate;
    }

    public Mob getMob() {
        return mob;
    }
}
