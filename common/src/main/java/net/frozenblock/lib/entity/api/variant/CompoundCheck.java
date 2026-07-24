package net.frozenblock.lib.entity.api.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;

public record CompoundCheck(List<SpawnCondition> conditions) implements SpawnCondition {
	public static final MapCodec<CompoundCheck> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		SpawnCondition.CODEC.listOf().fieldOf("conditions").forGetter(CompoundCheck::conditions)
	).apply(instance, CompoundCheck::new));

	@Override
	public MapCodec<? extends SpawnCondition> codec() {
		return MAP_CODEC;
	}

	@Override
	public boolean test(SpawnContext spawnContext) {
		return this.conditions.stream().allMatch(condition -> condition.test(spawnContext));
	}
}
