/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.modmenu.api;

import java.util.ArrayList;
import net.frozenblock.lib.modmenu.impl.FrozenModMenuBadge;

public abstract class FrozenModMenuEntrypoint {

	public abstract ArrayList<FrozenModMenuBadge> newBadges();

	public static FrozenModMenuBadge createBadge(String translationKey, int outlineColor, int fillColor, String key) {
		return new FrozenModMenuBadge(translationKey, outlineColor, fillColor, key);
	}

}

