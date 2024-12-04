package net.frozenblock.lib.worldgen.biome.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.lib.worldgen.biome.impl.BiomeInterface;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BiomeManager.class)
public class BiomeManagerMixin {

	@ModifyExpressionValue(
		method = "getBiome",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/biome/BiomeManager$NoiseBiomeSource;getNoiseBiome(III)Lnet/minecraft/core/Holder;"
		)
	)
	public Holder<Biome> frozenLib$appendBiomeID(Holder<Biome> original) {
		if ((Object) original.value() instanceof BiomeInterface biomeInterface) {
			original.unwrap().left().ifPresent(biomeResourceKey -> biomeInterface.frozenLib$setBiomeID(biomeResourceKey.location()));
		}
		return original;
	}
}
