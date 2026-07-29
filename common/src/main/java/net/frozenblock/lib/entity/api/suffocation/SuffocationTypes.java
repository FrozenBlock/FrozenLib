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

package net.frozenblock.lib.entity.api.suffocation;

import java.util.List;
import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.entity.impl.suffocation.SuffocationType;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

@UtilityClass
public final class SuffocationTypes {
	public static final ResourceKey<SuffocationType> WATER = createKey(FrozenLibConstants.id("water"));

	public static Registry<SuffocationType> registry(RegistryAccess registryAccess) {
		return registryAccess.lookupOrThrow(FrozenLibRegistries.SUFFOCATION_TYPE);
	}

	public static List<SuffocationType> getAll(RegistryAccess registryAccess) {
		return registry(registryAccess).stream().toList();
	}

	public static Holder<SuffocationType> get(RegistryAccess registryAccess, ResourceKey<SuffocationType> key) {
		return registry(registryAccess).getOrThrow(key);
	}

	public static ResourceKey<SuffocationType> createKey(Identifier id) {
		return ResourceKey.create(FrozenLibRegistries.SUFFOCATION_TYPE, id);
	}

	public static void register(BootstrapContext<SuffocationType> context, ResourceKey<SuffocationType> name, SuffocationType type) {
		context.register(name, type);
	}

	public static void register(BootstrapContext<SuffocationType> context, ResourceKey<SuffocationType> name, SuffocationType.Builder builder) {
		register(context, name, builder.build());
	}

	public static void bootstrap(BootstrapContext<SuffocationType> context) {
		register(
			context,
			WATER,
			SuffocationType.builder(MeterStyle.DRAIN, 15, 300)
				.airBehavior(AirBehavior.DISPLAY_ONLY)
				.capacity(300)
				.meter(
					Identifier.withDefaultNamespace("hud/air"),
					null,
					Identifier.withDefaultNamespace("hud/air_empty"),
					Identifier.withDefaultNamespace("hud/air_bursting")
				)
		);
	}
}
