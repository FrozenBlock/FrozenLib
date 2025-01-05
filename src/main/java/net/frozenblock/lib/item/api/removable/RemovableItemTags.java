/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.item.api.removable;

import java.util.LinkedHashMap;
import java.util.Set;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Targets {@link DataComponents#CUSTOM_DATA }
 */
public class RemovableItemTags {

	private static final LinkedHashMap<String, RemovableItemTag> REMOVABLE_ITEM_TAGS = new LinkedHashMap<>();

	public static void register(String tagKey, RemovalPredicate removalPredicate, boolean removeOnStackMerge) {
		REMOVABLE_ITEM_TAGS.put(tagKey, new RemovableItemTag(tagKey, removalPredicate, removeOnStackMerge));
	}

	public static boolean canRemoveTag(String tagKey, Level level, Entity entity, int slot, boolean selected) {
		RemovableItemTag removableItemTag = REMOVABLE_ITEM_TAGS.get(tagKey);
		if (removableItemTag != null) {
			return removableItemTag.shouldRemove(level, entity, slot, selected);
		} else {
			FrozenLogUtils.logError("Unable to find RemovableItemTag data for TagKey " + tagKey + "!", true, null);
			FrozenLogUtils.logError("Please make sure " + tagKey + " is registered in RemovableItemTags.class!", true, null);
			return false;
		}
	}

	public static boolean shouldRemoveTagOnStackMerge(String tagKey) {
		RemovableItemTag removableItemTag = REMOVABLE_ITEM_TAGS.get(tagKey);
		if (removableItemTag != null) {
			return removableItemTag.shouldRemoveOnStackMerge();
		} else {
			FrozenLogUtils.logError("Unable to find RemovableItemTag data for TagKey " + tagKey + "!", true, null);
			FrozenLogUtils.logError("Please make sure " + tagKey + " is registered in RemovableItemTags.class!", true, null);
			return true;
		}
	}

	public static Set<String> keys() {
		return REMOVABLE_ITEM_TAGS.keySet();
	}

	public static class RemovableItemTag implements RemovalPredicate {
		private final String tagKey;
		private final RemovalPredicate predicate;
		private final boolean removeOnStackMerge;

		public RemovableItemTag(String tagKey, RemovalPredicate predicate, boolean removeOnStackMerge) {
			this.tagKey = tagKey;
			this.predicate = predicate;
			this.removeOnStackMerge = removeOnStackMerge;
		}

		public String getTagKey() {
			return this.tagKey;
		}

		@Override
		public boolean shouldRemove(Level level, Entity entity, int slot, boolean selected) {
			return this.predicate.shouldRemove(level, entity, slot, selected);
		}

		public boolean shouldRemoveOnStackMerge() {
			return this.removeOnStackMerge;
		}
	}
}
