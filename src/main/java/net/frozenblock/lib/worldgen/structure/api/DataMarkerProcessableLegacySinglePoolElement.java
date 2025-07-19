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

package net.frozenblock.lib.worldgen.structure.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.worldgen.structure.impl.FrozenStructurePoolElementTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

/**
 * Be sure to mixin into {@link StructurePoolElement#handleDataMarker(LevelAccessor, StructureTemplate.StructureBlockInfo, BlockPos, Rotation, RandomSource, BoundingBox)}!
 */
public class DataMarkerProcessableLegacySinglePoolElement extends SinglePoolElement {
	public static final MapCodec<DataMarkerProcessableLegacySinglePoolElement> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(templateCodec(), processorsCodec(), projectionCodec(), overrideLiquidSettingsCodec())
			.apply(instance, DataMarkerProcessableLegacySinglePoolElement::new)
	);

	public DataMarkerProcessableLegacySinglePoolElement(
		Either<ResourceLocation, StructureTemplate> either,
		Holder<StructureProcessorList> holder,
		StructureTemplatePool.Projection projection,
		Optional<LiquidSettings> optional
	) {
		super(either, holder, projection, optional);
	}

	@Override
	protected @NotNull StructurePlaceSettings getSettings(Rotation rotation, BoundingBox boundingBox, LiquidSettings liquidSettings, boolean offset) {
		StructurePlaceSettings structurePlaceSettings = super.getSettings(rotation, boundingBox, liquidSettings, offset);
		structurePlaceSettings.popProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
		structurePlaceSettings.addProcessor(BlockIgnoreProcessor.AIR);
		return structurePlaceSettings;
	}

	@Override
	public @NotNull StructurePoolElementType<?> getType() {
		return FrozenStructurePoolElementTypes.DATA_MARKER_PROCESSABLE_LEGACY_SINGLE;
	}

	@Override
	public @NotNull String toString() {
		return "DataMarkerProcessableLegacySingle[" + this.template + "]";
	}
}
