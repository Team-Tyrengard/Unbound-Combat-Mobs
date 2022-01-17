package com.tyrengard.unbound.combat.mobs;

import com.tyrengard.unbound.combat.mobs.enums.MobType;
import com.tyrengard.unbound.combat.mobs.events.UCMobSpawnedEvent;
import com.tyrengard.unbound.combat.mobs.spawning.SpawnBehavior;
import com.tyrengard.unbound.combat.CombatEngine;
import com.tyrengard.unbound.combat.enums.CombatAttribute;
import com.tyrengard.unbound.combat.combatant.skills.UCSkill;
import com.tyrengard.unbound.combat.stats.CombatStat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MobTemplate implements Comparable<MobTemplate>{
    protected String name;
    protected final EntityType base;
    protected final MobType type;
    protected final String shortDescription;
    protected final List<String> fullDescription;

    protected final int spawnPriority;
    protected final String[] allowedWorldNames;
    protected final boolean despawn;
    protected final Hashtable<Biome, SpawnBehavior> spawnBehaviors;

    // TODO: add combat behavior

    protected final Hashtable<CombatStat, Double> stats;
    protected final Hashtable<CombatAttribute, Short> attributes;
    protected final List<UCSkill> skills;

    public MobTemplate(String name, EntityType base, MobType type, String shortDescription, List<String> fullDescription,
                       int spawnPriority, String[] allowedWorldNames, boolean despawn, Hashtable<Biome, SpawnBehavior> spawnBehaviors,
                       Hashtable<CombatStat, Double> stats, Hashtable<CombatAttribute, Short> attributes, List<UCSkill> skills) {
        this.name = name;
        this.base = base;
        this.type = type;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;

        this.spawnPriority = spawnPriority;
        this.allowedWorldNames = allowedWorldNames;
        this.despawn = despawn;
        this.spawnBehaviors = spawnBehaviors;

        this.stats = stats;
        this.attributes = attributes;
        this.skills = skills;
    }

    protected void spawn(Location loc) {
        World world = loc.getWorld();
        Mob mob = (Mob) world.spawn(loc, Objects.requireNonNull(base.getEntityClass()), entity -> {
            UCMob ucMob = new UCMob((Mob) entity);
            for (CombatStat stat : stats.keySet())
                ucMob.setValueForStat(stat, stats.getOrDefault(stat, 0.0));
            CombatEngine.addCombatant(ucMob);
        });

        Bukkit.getPluginManager().callEvent(new UCMobSpawnedEvent(this, mob));
    }

    public static MobTemplate fromYamlFile(File file) {
        try {
            YamlConfiguration mobTemplateYaml = YamlConfiguration.loadConfiguration(file);

            String name = mobTemplateYaml.getString("name");
            EntityType base = EntityType.valueOf(Objects.requireNonNull(mobTemplateYaml.getString("base")).toUpperCase());
            MobType type = MobType.valueOf(Objects.requireNonNull(mobTemplateYaml.getString("type")).toUpperCase());
            String shortDescription = mobTemplateYaml.getString("short-description");
            List<String> fullDescription = mobTemplateYaml.getStringList("full-description");

            ConfigurationSection behaviorSection = mobTemplateYaml.getConfigurationSection("behavior");
            int spawnPriority = 0;
            String[] allowedWorldNames = new String[0];
            boolean despawn = true;
            Hashtable<Biome, SpawnBehavior> spawnBehaviors = new Hashtable<>();
            if (behaviorSection != null) {
                ConfigurationSection spawningSection = behaviorSection.getConfigurationSection("spawning");
                if (spawningSection != null) {
                    spawnPriority = spawningSection.getInt("priority");
                    allowedWorldNames = spawningSection.getStringList("worlds").toArray(String[]::new);
                    despawn = spawningSection.getBoolean("despawn", true);
                    ConfigurationSection defaultSpawningBehaviorSection = spawningSection.getConfigurationSection("default");
                    for (Biome biome : Biome.values()) {
                        String biomeKey = biome.name().toLowerCase().replaceAll("_", "-");
                        ConfigurationSection biomeSpecificSpawningBehaviorSection = spawningSection.getConfigurationSection(biomeKey);
                        if (biomeSpecificSpawningBehaviorSection != null)
                            spawnBehaviors.put(biome, SpawnBehavior.fromConfigurationSection(biome, biomeSpecificSpawningBehaviorSection));
                        else if (defaultSpawningBehaviorSection != null)
                            spawnBehaviors.put(biome, SpawnBehavior.fromConfigurationSection(biome, defaultSpawningBehaviorSection));
                    }
                }

                // TODO: add combat behavior
            }

            ConfigurationSection statsSection = mobTemplateYaml.getConfigurationSection("stats");
            Hashtable<CombatStat, Double> stats = new Hashtable<>(Objects.requireNonNull(statsSection).getKeys(false).size());
            for (String key : statsSection.getKeys(false)) {
                CombatStat stat = CombatStat.valueOf(key.toUpperCase());
                stats.put(stat, statsSection.getDouble(key, stat.baseValue()));
            }

            ConfigurationSection attributesSection = mobTemplateYaml.getConfigurationSection("attributes");
            Hashtable<CombatAttribute, Short> attributes;
            if (attributesSection == null) attributes = new Hashtable<>(0);
            else attributes = new Hashtable<>(attributesSection.getKeys(false).size());

            return new MobTemplate(
                    name, base, type, shortDescription, fullDescription,
                    spawnPriority, allowedWorldNames, despawn, spawnBehaviors,
                    stats, attributes, Collections.emptyList()
            );
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public int compareTo(@NotNull MobTemplate o) {
        return Comparator.comparing((MobTemplate mt) -> mt.spawnPriority)
                        .reversed()
                        .compare(this, o);
    }
}
