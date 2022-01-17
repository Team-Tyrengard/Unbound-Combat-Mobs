package com.tyrengard.unbound.combat.mobs.spawning;

import com.tyrengard.unbound.combat.mobs.MobManager;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public record SpawnBehavior(Biome biome,
                            SpawnStrategy strategy,
                            SpawnCondition... spawnConditions) {
    public static SpawnBehavior fromConfigurationSection(Biome biome, ConfigurationSection cs) throws NullPointerException {
        SpawnStrategy strategy;
        String strategyString = cs.getString("strategy");
        if (strategyString != null) {
            strategy = switch (strategyString) {
                case "replace" -> SpawnStrategy.REPLACE;
                case "forced" -> SpawnStrategy.FORCED;
                default -> MobManager.getCustomSpawnStrategy(strategyString);
            };
        } else strategy = SpawnStrategy.REPLACE;

        List<SpawnCondition> spawnConditions = new ArrayList<>();
        ConfigurationSection conditionsSection = cs.getConfigurationSection("conditions");
        if (conditionsSection != null) {
            if (conditionsSection.contains("spawn-rate")) {
                double spawnRate = conditionsSection.getDouble("spawn-rate");
                spawnConditions.add(new SpawnRateCondition(spawnRate));
            }
            if (conditionsSection.contains("max-mob-count")) {
                int max = conditionsSection.getInt("max-mob-count");
                spawnConditions.add(new MobCountCondition(max));
            }
        }

        return new SpawnBehavior(biome, strategy, spawnConditions.toArray(SpawnCondition[]::new));
    }
}
