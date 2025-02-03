package net.frozenblock.lib.datagen.api;

import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

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

	public final void createTintedFlowerBed(@NotNull BlockModelGenerators generator, @NotNull Block block) {
		generator.createSimpleFlatItemModel(block.asItem());
		ResourceLocation resourceLocation = TINTED_FLOWERBED_1.create(block, generator.modelOutput);
		ResourceLocation resourceLocation2 = TINTED_FLOWERBED_2.create(block, generator.modelOutput);
		ResourceLocation resourceLocation3 = TINTED_FLOWERBED_3.create(block, generator.modelOutput);
		ResourceLocation resourceLocation4 = TINTED_FLOWERBED_4.create(block, generator.modelOutput);
		generator.blockStateOutput
			.accept(
				MultiPartGenerator.multiPart(block)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation2)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation3)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation4)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
					)
					.with(
						Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
						Variant.variant().with(VariantProperties.MODEL, resourceLocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
					)
			);
	}

	@Contract("_, _, _ -> new")
	private static @NotNull ModelTemplate createModelTemplate(String string, String string2, TextureSlot... textureSlots) {
		return new ModelTemplate(Optional.of(FrozenLibConstants.id("block/" + string)), Optional.of(string2), textureSlots);
	}
}
