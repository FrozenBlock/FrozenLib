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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl;

import net.fabricmc.api.ModInitializer;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public class QuiltSurfaceRuleInitializer {
    public static void onInitialize() {
        Registry.register(Registry.RULE, FrozenMain.id("reloadable_sequence"),
                ReloadableSequenceMaterialRule.RULE_CODEC.codec()
        );
    }
}
