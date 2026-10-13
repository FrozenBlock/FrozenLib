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

package net.frozenblock.lib.sound.api.predicate;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

// FIXME: use proper codecs & ml registry methods
public abstract class SoundPredicate<T extends Entity> {

	public abstract boolean test(T entity);

	@Nullable
	public Boolean firstTickTest(T entity) {
		return null;
	}

	public void onStart(@Nullable T entity) {}

	public void onStop(@Nullable T entity) {}
}
