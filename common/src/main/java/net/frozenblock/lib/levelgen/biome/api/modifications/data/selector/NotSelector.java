package net.frozenblock.lib.levelgen.biome.api.modifications.data.selector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.levelgen.biome.api.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record NotSelector(Holder<BiomeSelector> selector) implements BiomeSelector {
	public static final MapCodec<NotSelector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		HOLDER_CODEC.fieldOf("selector").forGetter(predicate -> predicate.selector)
	).apply(instance, NotSelector::new));

	@Override
	public boolean test(BiomeSelectionContext context) {
		return !this.selector.value().test(context);
	}

	@Override
	public MapCodec<? extends BiomeSelector> codec() {
		return CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "selector", this.selector);
	}
}
