package com.tyrengard.unbound.combat.mobs;

import com.tyrengard.aureycore.foundation.AManagedPlugin;
import com.tyrengard.unbound.combat.mobs.commands.UCMCommands;
import org.bukkit.configuration.InvalidConfigurationException;

public class UnboundCombatMobs extends AManagedPlugin {
    private static UnboundCombatMobs instance;

    @Override
    protected void onPluginEnable() throws InvalidConfigurationException {
        instance = this;

        addManager(new MobManager(this));

        saveResource("mob-template.yml", true);
    }

    @Override
    protected void onPostEnable() {
        addACommandExecutor(new UCMCommands(), "unbound-combat-mobs-admin", "unbound-combat-mobs");
    }

    @Override
    protected void onPluginDisable() {

    }

    public static void reloadPlugin() {
        instance.reload();
    }
}
