package com.tyrengard.unbound.combat.mobs.commands;

import com.tyrengard.aureycore.foundation.ACommandExecutor;
import com.tyrengard.aureycore.foundation.CommandDeclaration;
import com.tyrengard.aureycore.foundation.common.utils.StringUtils;
import com.tyrengard.unbound.combat.mobs.MobManager;
import com.tyrengard.unbound.combat.mobs.UnboundCombatMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class UCMCommands extends ACommandExecutor {
    private static final String SEPARATOR_LEFT = "\u297C", SEPARATOR_RIGHT = "\u297D";
    private static final String UNBOUND_HELP_HEADER = ChatColor.DARK_PURPLE + SEPARATOR_LEFT
            + StringUtils.padString(ChatColor.AQUA + "Unbound Combat Mobs - Commands" + ChatColor.DARK_PURPLE, '-',
            StringUtils.MAX_CHAT_SIZE, StringUtils.StringPaddingOptions.CENTER) + SEPARATOR_RIGHT;

    public UCMCommands() {
        super();

        Bukkit.getLogger().info("Adding /unbound-combat-mobs-admin commands...");
        addAdminCommands();
        Bukkit.getLogger().info("Adding /unbound-combat-mobs commands...");
        addRegularCommands();

        createHelpCommands(UNBOUND_HELP_HEADER, ChatColor.DARK_PURPLE, ChatColor.AQUA);
    }

    public void addAdminCommands() {
        // Reload
        addRegularCommand(new CommandDeclaration<>(false, "unbound-combat-mobs-admin",
                "reload", "Reload plugin",
                new String[] {}, (sender, args) -> {
            UnboundCombatMobs.reloadPlugin();
            return true;
        }));

        addPlayerCommand(new CommandDeclaration<>("unbound-combat-mobs-admin",
                "spawn", "Spawn a mob from a loaded template",
                new String[] {}));
        for (String mobTemplateName : MobManager.getMobTemplates().keySet()) {
            addPlayerCommand(new CommandDeclaration<>(true, "unbound-combat-mobs-admin",
                "spawn " + mobTemplateName, (sender, args) -> {
                MobManager.spawnMob(mobTemplateName, sender.getLocation());
                sender.sendMessage("Spawned " + mobTemplateName + " at player location.");
                return true;
            }));
        }
    }

    public void addRegularCommands() {

    }
}
