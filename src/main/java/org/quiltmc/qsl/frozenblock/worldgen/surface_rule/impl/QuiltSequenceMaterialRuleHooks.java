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

import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.frozenblock.core.base.api.util.InjectedInterface;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
@InjectedInterface(SurfaceRules.SequenceRuleSource.class)
public interface QuiltSequenceMaterialRuleHooks {
    void frozenblock_quilt$freeze();
}
