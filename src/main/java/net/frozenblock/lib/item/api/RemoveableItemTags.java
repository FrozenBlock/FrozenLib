/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.item.api;

import java.util.LinkedHashMap;
import java.util.Set;
import net.frozenblock.lib.FrozenLogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class RemoveableItemTags {

	private static final LinkedHashMap<String, RemoveableItemTag> REMOVABLE_ITEM_TAGS = new LinkedHashMap<>();

	public static void register(String tagKey, RemovalPredicate removalPredicate, boolean removeOnStackMerge) {
		REMOVABLE_ITEM_TAGS.put(tagKey, new RemoveableItemTag(tagKey, removalPredicate, removeOnStackMerge));
	}

	public static boolean canRemoveTag(String tagKey, Level level, Entity entity, int slot, boolean selected) {
		RemoveableItemTag removeableItemTag = REMOVABLE_ITEM_TAGS.get(tagKey);
		if (removeableItemTag != null) {
			return removeableItemTag.shouldRemove(level, entity, slot, selected);
		} else {
			FrozenLogUtils.logError("Unable to find RemoveableItemTag data for TagKey " + tagKey + "!", true, null);
			FrozenLogUtils.logError("Please make sure " + tagKey + " is registered in RemoveableItemTags.class!", true, null);
			return false;
		}
	}

	public static boolean shouldRemoveTagOnStackMerge(String tagKey) {
		RemoveableItemTag removeableItemTag = REMOVABLE_ITEM_TAGS.get(tagKey);
		if (removeableItemTag != null) {
			return removeableItemTag.shouldRemoveOnStackMerge();
		} else {
			FrozenLogUtils.logError("Unable to find RemoveableItemTag data for TagKey " + tagKey + "!", true, null);
			FrozenLogUtils.logError("Please make sure " + tagKey + " is registered in RemoveableItemTags.class!", true, null);
			return true;
		}
	}

	public static Set<String> keys() {
		return REMOVABLE_ITEM_TAGS.keySet();
	}

	public static class RemoveableItemTag implements RemovalPredicate {
		private final String tagKey;
		private final RemovalPredicate predicate;
		private final boolean removeOnStackMerge;

		public RemoveableItemTag(String tagKey, RemovalPredicate predicate, boolean removeOnStackMerge) {
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

	@FunctionalInterface
	public interface RemovalPredicate {
		boolean shouldRemove(Level level, Entity entity, int slot, boolean selected);
	}
}
