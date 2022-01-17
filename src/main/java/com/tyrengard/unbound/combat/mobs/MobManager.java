package com.tyrengard.unbound.combat.mobs;

import com.tyrengard.aureycore.foundation.AManager;
import com.tyrengard.aureycore.foundation.Configured;
import com.tyrengard.unbound.combat.mobs.events.UCMobSpawnedEvent;
import com.tyrengard.unbound.combat.mobs.spawning.SpawnBehavior;
import com.tyrengard.unbound.combat.mobs.spawning.SpawnCondition;
import com.tyrengard.unbound.combat.mobs.spawning.SpawnStrategy;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MobManager extends AManager<UnboundCombatMobs> implements Configured, Listener {
    private static final Hashtable<String, SpawnStrategy> customSpawnStrategies = new Hashtable<>();
    private static final Hashtable<String, MobTemplate> mobTemplates = new Hashtable<>();
    private static final Hashtable<EntityType, TreeSet<MobTemplate>> mobTemplatesByBase = new Hashtable<>();
    private static final Hashtable<MobTemplate, Integer> mobCounts = new Hashtable<>();

    MobManager(UnboundCombatMobs plugin) {
        super(plugin);
    }

    // region Manager overrides
    @Override
    protected void startup() {
        logDebug("Loading mob templates...");
        final Path mobTemplateFolderPath = Paths.get(plugin.getDataFolder().getPath(), "mobs");

        File mobTemplateFolder = mobTemplateFolderPath.toFile();
        if (!mobTemplateFolder.exists() && !mobTemplateFolder.mkdirs())
            logError("Unable to create \"mobs\" folder.");
        else {
            try {
                List<Path> mobTemplatePaths = Files.walk(mobTemplateFolderPath)
                    .filter(path -> {
                        System.out.println(path);
                        return true;
                    })
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".yml"))
                    .collect(Collectors.toList());

                logDebug("Found " + mobTemplatePaths.size() + " mob template files.");

                for (Path path : mobTemplatePaths) {
                    logDebug("Loading mob template from " + path);
                    MobTemplate mobTemplate = MobTemplate.fromYamlFile(path.toFile());
                    if (mobTemplate == null)
                        logWarning("Unable to read mob template from file \"" + path.getFileName() + "\".");
                    else
                        addMobTemplate(mobTemplate);
                }

                logDebug("Successfully loaded " + mobTemplates.size() + " mob templates.");
            } catch (IOException e) {
                logError("Unable to walk through \"mobs\" folder.");
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void cleanup() {

    }

    @Override
    public void setConfigDefaults(FileConfiguration config) {

    }

    @Override
    public void loadSettingsFromConfig(FileConfiguration config) throws InvalidConfigurationException {

    }

    @Override
    public void saveSettingsToConfig(FileConfiguration config) {

    }
    // endregion

    // region Event handlers

    @EventHandler(ignoreCancelled = true)
    private void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
            return;

        TreeSet<MobTemplate> mobTemplates = mobTemplatesByBase.get(e.getEntityType());
        if (mobTemplates == null || mobTemplates.isEmpty())
            return;

        Location loc = e.getLocation();
        for (MobTemplate mobTemplate : mobTemplates) {
            SpawnBehavior spawnBehavior = mobTemplate.spawnBehaviors.get(loc.getBlock().getBiome());
            if (spawnBehavior == null || spawnBehavior.strategy() != SpawnStrategy.REPLACE)
                continue;

            boolean shouldSpawn = true;
            for (SpawnCondition condition : spawnBehavior.spawnConditions()) {
                if (!condition.passCheck(mobTemplate)) {
                    shouldSpawn = false;
                    break;
                }
            }
            if (shouldSpawn) {
                mobTemplate.spawn(loc);
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDeath(EntityDeathEvent event) {

    }

    @EventHandler
    private void onUCMobSpawned(UCMobSpawnedEvent e) {
        mobCounts.compute(e.getMobTemplate(), (k, v) -> v == null ? 1 : v + 1);
    }
    // endregion

    public static SpawnStrategy getCustomSpawnStrategy(String name) {
        return customSpawnStrategies.get(name);
    }

    public static void addMobTemplate(MobTemplate mobTemplate) {
        String mobTemplateName = mobTemplate.name.toLowerCase().replaceAll(" ", "-");
        mobTemplates.put(mobTemplateName, mobTemplate);

        mobTemplatesByBase.putIfAbsent(mobTemplate.base, new TreeSet<>()).add(mobTemplate);
    }

    public static void spawnMob(String mobName, Location loc) {
        MobTemplate mobTemplate = mobTemplates.get(mobName);
        mobTemplate.spawn(loc);
    }

    public static int getMobCount(MobTemplate mobTemplate) {
        return mobCounts.getOrDefault(mobTemplate, 0);
    }

    public static Hashtable<String, MobTemplate> getMobTemplates() {
        return new Hashtable<>(mobTemplates);
    }
}
