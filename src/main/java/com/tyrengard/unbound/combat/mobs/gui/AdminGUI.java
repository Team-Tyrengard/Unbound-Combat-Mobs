package com.tyrengard.unbound.combat.mobs.gui;

import com.tyrengard.aureycore.guis.ACustomChestGUI;
import org.bukkit.inventory.ItemStack;

/**
 * Custom chest GUI for admins of UnboundCombatMobs
 */
public final class AdminGUI extends ACustomChestGUI {
    public AdminGUI(int size, int pages, int currentPage) {
        super(size, pages, currentPage);
    }

    @Override
    protected void onAssign() {

    }

    @Override
    protected String getName(int page) {
        return null;
    }

    @Override
    protected ItemStack[] getContents(int page) {
        return new ItemStack[0];
    }
}
