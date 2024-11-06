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

package net.frozenblock.lib.worldgen.structure.impl.upgrade;

import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.structures.SnbtToNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class CommandStructureUpdater implements SnbtToNbt.Filter {
	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public @NotNull CompoundTag apply(String string, CompoundTag compoundTag) {
		return update(string, compoundTag);
	}

	public static @NotNull CompoundTag update(String name, CompoundTag nbt) {
		StructureTemplate structureTemplate = new StructureTemplate();
		int i = NbtUtils.getDataVersion(nbt, 500);
		int currentVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
		if (i < currentVersion) {
			LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, currentVersion, name);
		}

		CompoundTag compoundTag = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), nbt, i);
		structureTemplate.load(BuiltInRegistries.BLOCK.asLookup(), compoundTag);
		return structureTemplate.save(new CompoundTag());
	}
}
