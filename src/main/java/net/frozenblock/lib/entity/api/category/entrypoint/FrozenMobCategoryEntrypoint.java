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

package net.frozenblock.lib.entity.api.category.entrypoint;

import java.util.ArrayList;
import net.frozenblock.lib.entity.impl.category.FrozenMobCategory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface FrozenMobCategoryEntrypoint {

	void newCategories(ArrayList<FrozenMobCategory> context);

	@Contract("_, _, _, _, _ -> new")
	static @NotNull FrozenMobCategory createCategory(ResourceLocation key, int max, boolean isFriendly, boolean isPersistent, int despawnDistance) {
		return new FrozenMobCategory(key, max, isFriendly, isPersistent, despawnDistance);
	}

}

