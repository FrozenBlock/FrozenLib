package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.HolderSet;

public class AllOfSelector extends AggregateBiomeSelector {
	public static final MapCodec<AllOfSelector> CODEC = createCodec(AllOfSelector::new);

	protected AllOfSelector(HolderSet<BiomeSelector> selectors) {
		super(selectors);
	}

	@Override
	public boolean test(BiomeSelectionContext context) {
		return this.selectors.stream().allMatch(selector -> selector.value().test(context));
	}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}
}
