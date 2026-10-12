/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.feature.api.feature.patch;

import com.mojang.datafixers.util.Function11;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FlexibleVegetationPatchFeature extends VegetationPatchFeature {
	public static final MapCodec<FlexibleVegetationPatchFeature> CODEC = makeCodec(FlexibleVegetationPatchFeature::new);
	public final FlexibleComponents flexibleComponents;

	protected static <T extends FlexibleVegetationPatchFeature> MapCodec<T> makeCodec(
		Function11<
			HolderSet<Block>,
			Holder<BlockStateProvider>,
			Holder<PlacedFeature>,
			CaveSurface,
			IntProvider,
			Float,
			Integer,
			Float,
			IntProvider,
			Float,
			FlexibleComponents,
			T
		> constructor
	) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("replaceable").forGetter(feature -> feature.replaceable),
			BlockStateProvider.CODEC.fieldOf("ground_state").forGetter(feature -> feature.groundState),
			PlacedFeature.CODEC.fieldOf("vegetation_feature").forGetter(feature -> feature.vegetationFeature),
			CaveSurface.CODEC.fieldOf("surface").forGetter(feature -> feature.surface),
			IntProviders.codec(1, 128).fieldOf("depth").forGetter(feature -> feature.depth),
			Codec.floatRange(0F, 1F).fieldOf("extra_bottom_block_chance").forGetter(feature -> feature.extraBottomBlockChance),
			Codec.intRange(1, 256).fieldOf("vertical_range").forGetter(feature -> feature.verticalRange),
			Codec.floatRange(0F, 1F).fieldOf("vegetation_chance").forGetter(feature -> feature.vegetationChance),
			IntProviders.CODEC.fieldOf("xz_radius").forGetter(feature -> feature.xzRadius),
			Codec.floatRange(0F, 1F).fieldOf("extra_edge_column_chance").forGetter(feature -> feature.extraEdgeColumnChance),
			FlexibleComponents.CODEC.fieldOf("flexible_components").forGetter(feature -> feature.flexibleComponents)
		).apply(instance, constructor));
	}

	public FlexibleVegetationPatchFeature(
		HolderSet<Block> replaceable,
		Holder<BlockStateProvider> groundState,
		Holder<PlacedFeature> vegetationFeature,
		CaveSurface surface,
		IntProvider depth,
		float extraBottomBlockChance,
		int verticalRange,
		float vegetationChance,
		IntProvider xzRadius,
		float extraEdgeColumnChance,
		FlexibleComponents flexibleComponents
	) {
		super(replaceable, groundState, vegetationFeature, surface, depth, extraBottomBlockChance, verticalRange, vegetationChance, xzRadius, extraEdgeColumnChance);
		this.flexibleComponents = flexibleComponents;
	}

	@Override
	public MapCodec<? extends FlexibleVegetationPatchFeature> codec() {
		return CODEC;
	}

	public record FlexibleComponents(
		Optional<BlockPredicate> isOpen,
		boolean placeOnEdges,
		boolean circular,
		boolean placeBaseBlock
	) {
		public static final FlexibleComponents DEFAULT = new FlexibleComponents(
			Optional.empty(), false, false, true
		);
		public static final FlexibleComponents CIRCULAR = new FlexibleComponents(
			Optional.empty(), false, true, true
		);
		public static final FlexibleComponents CIRCULAR_NO_BASE_BLOCK = new FlexibleComponents(
			Optional.empty(), false, false, false
		);
		public static final FlexibleComponents NO_BASE_BLOCK = new FlexibleComponents(
			Optional.empty(), false, false, false
		);
		public static final FlexibleComponents PLACE_ON_EDGES = new FlexibleComponents(
			Optional.empty(), true, false, true
		);
		public static final Codec<FlexibleComponents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockPredicate.CODEC.optionalFieldOf("is_open").forGetter(FlexibleComponents::isOpen),
			Codec.BOOL.optionalFieldOf("place_on_edges", false).forGetter(FlexibleComponents::placeOnEdges),
			Codec.BOOL.optionalFieldOf("circular", false).forGetter(FlexibleComponents::circular),
			Codec.BOOL.optionalFieldOf("place_base_block", true).forGetter(FlexibleComponents::placeBaseBlock)
		).apply(instance, FlexibleComponents::new));
	}
}
