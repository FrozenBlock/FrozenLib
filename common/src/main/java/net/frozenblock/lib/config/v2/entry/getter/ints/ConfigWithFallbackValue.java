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

package net.frozenblock.lib.config.v2.entry.getter.ints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public record ConfigWithFallbackValue(
	ID entryId,
	Optional<Holder<ContextIntProvider>> whenPresent,
	Holder<ContextIntProvider> fallback
) implements ContextIntProvider {
	public static final MapCodec<ConfigWithFallbackValue> MAP_CODEC = ID.CODEC.fieldOf("entry").dispatchMap(
		value -> value.entryId,
		id -> RecordCodecBuilder.mapCodec(instance -> {
			final ConfigEntry<?> entry = ConfigV2Registry.getEntry(id);
			if (entry == null) {
				return instance.group(
					instance.point(id),
					ContextIntProviders.CODEC.fieldOf("fallback").forGetter(provider -> provider.fallback)
				).apply(instance, ConfigWithFallbackValue::whenNotPresent);
			}

			return instance.group(
				instance.point(id),
				ContextIntProviders.CODEC.fieldOf("when_present").forGetter(provider -> provider.whenPresent.orElseThrow()),
				ContextIntProviders.CODEC.fieldOf("fallback").forGetter(provider -> provider.fallback)
			).apply(instance, ConfigWithFallbackValue::of);
		})
	);

	private static ConfigWithFallbackValue whenNotPresent(ID entryId, Holder<ContextIntProvider> fallback) {
		return new ConfigWithFallbackValue(entryId, Optional.empty(), fallback);
	}

	public static ConfigWithFallbackValue of(ID entryId, Holder<ContextIntProvider> whenPresent, Holder<ContextIntProvider> fallback) {
		return new ConfigWithFallbackValue(entryId, Optional.of(whenPresent), fallback);
	}

	@Override
	public int getIntUnsafe(LootContext lootContext) throws ArithmeticException {
		return this.whenPresent
			.map(Holder::value)
			.map(provider -> provider.getIntUnsafe(lootContext))
			.orElseGet(() -> this.fallback.value().getIntUnsafe(lootContext));
	}

	@Override
	public MapCodec<ConfigWithFallbackValue> codec() {
		return MAP_CODEC;
	}

	@Override
	public void validate(ValidationContext context) {
		if (this.whenPresent.isPresent()) Validatable.validateHolder(context, "when_present", this.whenPresent);
		Validatable.validateHolder(context, "fallback", this.fallback);
	}
}
