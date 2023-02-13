/*
 * Copyright 2023 FrozenBlock
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

package net.frozenblock.lib.testmod.config.cloth;

import net.frozenblock.lib.FrozenBools;

public final class ClothConfigInteractionHandler {

    public static boolean testBoolean() {
        if (FrozenBools.HAS_CLOTH_CONFIG) {
            return TestConfig.get().general.testBoolean;
        }
        return true;
    }

    public static boolean testSubMenuBoolean() {
        if (FrozenBools.HAS_CLOTH_CONFIG) {
            return TestConfig.get().general.subMenu.testSubMenuBoolean;
        }
        return true;
    }
}
