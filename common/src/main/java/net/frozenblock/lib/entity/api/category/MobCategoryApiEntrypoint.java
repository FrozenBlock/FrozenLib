/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.entity.api.category;

import java.util.ArrayList;
import java.util.function.Consumer;
import net.frozenblock.lib.entrypoint.api.Entrypoint;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.ApiStatus;

/**
 * Used to modify or create new {@link MobCategory}s.
 * <p>
 * If creating new {@link MobCategory}s, see {@link MutableMobCategory#create(String, String, String, int, boolean, boolean, int, Consumer)}.
 */
@Entrypoint("frozenlib:mob_category")
public interface MobCategoryApiEntrypoint {

	void add(Context context);

	final class Context {
		private final ArrayList<MutableMobCategory> newCategories = new ArrayList<>();

		public void add(MutableMobCategory category) {
			this.newCategories.add(category);
		}

		public void add(
			String modId,
			String name,
			String debugAbbreviation,
			int max,
			boolean isFriendly,
			boolean isPersistent,
			int despawnDistance,
			Consumer<MobCategory> creationCallback
		) {
			this.add(MutableMobCategory.create(modId, name, debugAbbreviation, max, isFriendly, isPersistent, despawnDistance, creationCallback));
		}

		@ApiStatus.Internal
		public void forEach(Consumer<MutableMobCategory> consumer) {
			this.newCategories.forEach(consumer);
		}
	}
}
