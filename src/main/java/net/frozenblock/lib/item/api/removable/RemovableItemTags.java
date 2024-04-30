/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
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
