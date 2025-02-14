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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Optional;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FrozenLibModelHelper {
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
		generator.blockStateOutput
			.accept(
				MultiPartGenerator.multiPart(block)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						multiVariant1
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						multiVariant1.with(BlockModelGenerators.Y_ROT_90)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						multiVariant1.with(BlockModelGenerators.Y_ROT_180)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						multiVariant1.with(BlockModelGenerators.Y_ROT_270)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						multiVariant2
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						multiVariant2.with(BlockModelGenerators.Y_ROT_90)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						multiVariant2.with(BlockModelGenerators.Y_ROT_180)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						multiVariant2.with(BlockModelGenerators.Y_ROT_270)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						multiVariant3
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						multiVariant3.with(BlockModelGenerators.Y_ROT_90)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						multiVariant3.with(BlockModelGenerators.Y_ROT_180)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						multiVariant3.with(BlockModelGenerators.Y_ROT_270)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						multiVariant4
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						multiVariant4.with(BlockModelGenerators.Y_ROT_90)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						multiVariant4.with(BlockModelGenerators.Y_ROT_180)
					)
					.with(
						BlockModelGenerators.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						multiVariant4.with(BlockModelGenerators.Y_ROT_270)
					)
			);
	}

	@Contract("_, _, _ -> new")
	private static @NotNull ModelTemplate createModelTemplate(String string, String string2, TextureSlot... textureSlots) {
		return new ModelTemplate(Optional.of(FrozenLibConstants.id("block/" + string)), Optional.of(string2), textureSlots);
	}
}
