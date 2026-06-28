package net.frozenblock.lib.levelgen.biome.mixin;

import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeSpecialEffectsInterface;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Optional;

@Mixin(BiomeSpecialEffects.class)
public class BiomeSpecialEffectsMixin implements BiomeSpecialEffectsInterface {
	@Mutable
	@Shadow
	@Final
	private int waterColor;

	@Mutable
	@Shadow
	@Final
	private Optional<Integer> foliageColorOverride;

	@Mutable
	@Shadow
	@Final
	private Optional<Integer> dryFoliageColorOverride;

	@Mutable
	@Shadow
	@Final
	private Optional<Integer> grassColorOverride;

	@Mutable
	@Shadow
	@Final
	private BiomeSpecialEffects.GrassColorModifier grassColorModifier;

	@Override
	public void frozenLib$setWaterColor(int color) {
		this.waterColor = color;
	}

	@Override
	public void frozenLib$setFoliageColorOverride(Optional<Integer> color) {
		this.foliageColorOverride = color;
	}

	@Override
	public void frozenLib$setDryFoliageColorOverride(Optional<Integer> color) {
		this.dryFoliageColorOverride = color;
	}

	@Override
	public void frozenLib$setGrassColorOverride(Optional<Integer> color) {
		this.grassColorOverride = color;
	}

	@Override
	public void frozenLib$setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier colorModifier) {
		this.grassColorModifier = colorModifier;
	}
}
