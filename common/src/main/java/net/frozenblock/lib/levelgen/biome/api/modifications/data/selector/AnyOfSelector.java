package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.HolderSet;

public class AnyOfSelector extends AggregateBiomeSelector {
	public static final MapCodec<AnyOfSelector> CODEC = createCodec(AnyOfSelector::new);

	protected AnyOfSelector(HolderSet<BiomeSelector> selectors) {
		super(selectors);
	}

	@Override
	public boolean test(BiomeSelectionContext context) {
		return this.selectors.stream().anyMatch(selector -> selector.value().test(context));
	}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}
}
