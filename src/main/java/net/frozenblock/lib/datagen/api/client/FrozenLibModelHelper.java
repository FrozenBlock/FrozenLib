/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.datagen.api.client;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@Environment(EnvType.CLIENT)
public class FrozenLibModelHelper {
	public static final Map<Direction, VariantMutator> MULTIFACE_GENERATOR_NO_UV_LOCK = ImmutableMap.of(
		Direction.NORTH, BlockModelGenerators.NOP,
		Direction.EAST, BlockModelGenerators.Y_ROT_90,
		Direction.SOUTH, BlockModelGenerators.Y_ROT_180,
		Direction.WEST, BlockModelGenerators.Y_ROT_270,
		Direction.UP, BlockModelGenerators.X_ROT_270,
		Direction.DOWN, BlockModelGenerators.X_ROT_90
	);
	public static final TexturedModel.Provider TINTED_FLOWERBED_1 = TexturedModel.createDefault(
		TextureMapping::flowerbed,
		createModelTemplate("tinted_flowerbed_1", "_1", TextureSlot.FLOWERBED, TextureSlot.STEM)
	);
	public static final TexturedModel.Provider TINTED_FLOWERBED_2 = TexturedModel.createDefault(
		TextureMapping::flowerbed,
		createModelTemplate("tinted_flowerbed_2", "_2", TextureSlot.FLOWERBED, TextureSlot.STEM)
	);
	public static final TexturedModel.Provider TINTED_FLOWERBED_3 = TexturedModel.createDefault(
		TextureMapping::flowerbed,
		createModelTemplate("tinted_flowerbed_3", "_3", TextureSlot.FLOWERBED, TextureSlot.STEM)
	);
	public static final TexturedModel.Provider TINTED_FLOWERBED_4 = TexturedModel.createDefault(
		TextureMapping::flowerbed,
		createModelTemplate("tinted_flowerbed_4", "_4", TextureSlot.FLOWERBED, TextureSlot.STEM)
	);

	public static void createTintedFlowerBed(@NotNull BlockModelGenerators generator, @NotNull Block block) {
		ResourceLocation itemModel = generator.createFlatItemModel(block.asItem());
		generator.registerSimpleTintedItemModel(block, itemModel, new GrassColorSource());

		MultiVariant multiVariant1 = BlockModelGenerators.plainVariant(TINTED_FLOWERBED_1.create(block, generator.modelOutput));
		MultiVariant multiVariant2 = BlockModelGenerators.plainVariant(TINTED_FLOWERBED_2.create(block, generator.modelOutput));
		MultiVariant multiVariant3 = BlockModelGenerators.plainVariant(TINTED_FLOWERBED_3.create(block, generator.modelOutput));
		MultiVariant multiVariant4 = BlockModelGenerators.plainVariant(TINTED_FLOWERBED_4.create(block, generator.modelOutput));
		generator.createSegmentedBlock(
			block,
			multiVariant1,
			BlockModelGenerators.FLOWER_BED_MODEL_1_SEGMENT_CONDITION,
			multiVariant2,
			BlockModelGenerators.FLOWER_BED_MODEL_2_SEGMENT_CONDITION,
			multiVariant3,
			BlockModelGenerators.FLOWER_BED_MODEL_3_SEGMENT_CONDITION,
			multiVariant4,
			BlockModelGenerators.FLOWER_BED_MODEL_4_SEGMENT_CONDITION
		);
	}

	public static <T extends Property<?>> @Unmodifiable @NotNull Map<T, VariantMutator> selectMultifaceNoUvLockProperties(
		StateHolder<?, ?> stateHolder,
		Function<Direction, T> function
	) {
		ImmutableMap.Builder<T, VariantMutator> builder = ImmutableMap.builderWithExpectedSize(MULTIFACE_GENERATOR_NO_UV_LOCK.size());
		MULTIFACE_GENERATOR_NO_UV_LOCK.forEach((direction, variantMutator) -> {
			T property = function.apply(direction);
			if (stateHolder.hasProperty(property)) {
				builder.put(property, variantMutator);
			}
		});
		return builder.build();
	}

	@Contract("_, _, _ -> new")
	private static @NotNull ModelTemplate createModelTemplate(String string, String string2, TextureSlot... textureSlots) {
		return new ModelTemplate(Optional.of(FrozenLibConstants.id("block/" + string)), Optional.of(string2), textureSlots);
	}
}
