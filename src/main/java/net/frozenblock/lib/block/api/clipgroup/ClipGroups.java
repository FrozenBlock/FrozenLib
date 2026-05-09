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

package net.frozenblock.lib.block.api.clipgroup;

import java.util.List;
import net.frozenblock.lib.block.impl.clipgroup.ClipGroup;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public final class ClipGroups {

	public static List<ClipGroup> getAll(RegistryAccess registryAccess) {
		return registryAccess.lookupOrThrow(FrozenLibRegistries.CLIP_GROUP).stream().toList();
	}
	
	public static ResourceKey<ClipGroup> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.CLIP_GROUP, id);
	}

	public static void register(BootstrapContext<ClipGroup> context, ResourceKey<ClipGroup> name, HolderSet<Block> blocks) {
		context.register(name, new ClipGroup(blocks));
	}
}
