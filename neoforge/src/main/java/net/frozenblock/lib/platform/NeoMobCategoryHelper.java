/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import net.frozenblock.lib.entity.api.category.entrypoint.FrozenMobCategoryEntrypoint;
import net.frozenblock.lib.entity.impl.category.FrozenMobCategory;
import net.frozenblock.lib.platform.service.MobCategoryHelper;

public class NeoMobCategoryHelper implements MobCategoryHelper {

	@Override
	public List<FrozenMobCategory> gatherMobCategories() {
		final ArrayList<FrozenMobCategory> newCategories = new ArrayList<>();
		ServiceLoader.load(FrozenMobCategoryEntrypoint.class, NeoMobCategoryHelper.class.getClassLoader())
			.forEach(entrypoint -> {
				try {
					entrypoint.newCategories(newCategories);
				} catch (Throwable ignored) {
				}
			});
		return newCategories;
	}
}
