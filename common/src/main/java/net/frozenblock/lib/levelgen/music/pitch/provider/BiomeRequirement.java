/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.levelgen.music.pitch.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record BiomeRequirement(HolderSet<Biome> requiredBiomes, Holder<PitchProvider> provider) implements PitchProvider {
	public static final MapCodec<BiomeRequirement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RegistryCodecs.holderSet(Registries.BIOME).fieldOf("requirements").forGetter(BiomeRequirement::requiredBiomes),
		HOLDER_CODEC.fieldOf("provider").forGetter(BiomeRequirement::provider)
	).apply(instance, BiomeRequirement::new));


	@Override
	public MapCodec<BiomeRequirement> codec() {
		return MAP_CODEC;
	}

	@Override
	public float sample(Context context) {
		return this.provider.value().sample(context);
	}

	@Override
	public boolean applicable(Context context) {
		return this.requiredBiomes.contains(context.biome()) && this.provider.value().applicable(context);
	}

	@Override
	public void validate(ValidationContext context) {
		Validatable.validateHolder(context, "provider", this.provider);
	}
}
