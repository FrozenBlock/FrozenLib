package net.frozenblock.lib.entity.api.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;

public record ConfigCheck(ConfigPredicate configPredicate) implements SpawnCondition {
	public static final MapCodec<ConfigCheck> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ConfigPredicate.CODEC.fieldOf("predicate").forGetter(ConfigCheck::configPredicate)
	).apply(instance, ConfigCheck::new));

	@Override
	public MapCodec<? extends SpawnCondition> codec() {
		return MAP_CODEC;
	}

	@Override
	public boolean test(SpawnContext spawnContext) {
		return this.configPredicate.test();
	}
}
