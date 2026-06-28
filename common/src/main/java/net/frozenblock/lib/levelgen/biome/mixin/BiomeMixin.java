package net.frozenblock.lib.levelgen.biome.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.levelgen.biome.impl.FrozenGrassColorModifier;
import net.frozenblock.lib.levelgen.biome.impl.modifications.BiomeInterface;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public class BiomeMixin implements BiomeInterface {
	@Mutable
	@Shadow
	@Final
	private Biome.ClimateSettings climateSettings;

	@Unique
	private FrozenGrassColorModifier frozenLib$frozenGrassColorModifier;

	@ModifyReturnValue(
		method = "getGrassColor",
		at = @At(
			value = "RETURN"
		)
	)
	public int frozenLib$modifyGrassColor(int original, double x, double y) {
		if (this.frozenLib$frozenGrassColorModifier != null) return this.frozenLib$frozenGrassColorModifier.modifyGrassColor(x, y, original);
		return original;
	}

	@Override
	public void frozenLib$setFrozenGrassColorModifier(FrozenGrassColorModifier frozenGrassColorModifier) {
		this.frozenLib$frozenGrassColorModifier = frozenGrassColorModifier;
	}

	@Override
	public FrozenGrassColorModifier frozenLib$getFrozenGrassColorModifier() {
		return this.frozenLib$frozenGrassColorModifier;
	}

	@Override
	public Biome.ClimateSettings frozenLib$getClimateSettings() {
		return this.climateSettings;
	}

	@Override
	public void frozenLib$setClimateSettings(Biome.ClimateSettings settings) {
		this.climateSettings = settings;
	}
}
