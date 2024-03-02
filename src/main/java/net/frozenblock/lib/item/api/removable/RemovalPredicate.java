package net.frozenblock.lib.item.api.removable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface RemovalPredicate {
	boolean shouldRemove(Level level, Entity entity, int slot, boolean selected);
}
