/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.data.api;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public abstract class NumberProviderProvider implements DataProvider {
	protected final FabricPackOutput output;
	private final PackOutput.PathProvider pathProvider;
	private final CompletableFuture<HolderLookup.Provider> registryLookup;

	protected NumberProviderProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		this.output = output;
		this.pathProvider = output.createRegistryElementsPathProvider(Registries.NUMBER_PROVIDER);
		this.registryLookup = registryLookup;
	}

	public abstract void generateNumberProvider(HolderLookup.Provider registryLookup, Consumer<Holder> consumer);

	public abstract String namespace();

	public static Holder create(Identifier id, NumberProvider provider) {
		return new Holder(id, provider);
	}

	public static Holder create(ResourceKey<NumberProvider> key, NumberProvider provider) {
		return create(key.identifier(), provider);
	}

	public Holder create(String name, NumberProvider provider) {
		return create(Identifier.fromNamespaceAndPath(this.namespace(), name), provider);
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.registryLookup.thenCompose(lookup -> {
			final Set<Identifier> identifiers = Sets.newHashSet();
			final Set<Holder> numberProviders = Sets.newHashSet();

			generateNumberProvider(lookup, numberProviders::add);

			final RegistryOps<JsonElement> ops = lookup.createSerializationContext(JsonOps.INSTANCE);
			final List<CompletableFuture<?>> futures = new ArrayList<>();

			for (Holder numberProvider : numberProviders) {
				if (!identifiers.add(numberProvider.id())) throw new IllegalStateException("Duplicate number provider " + numberProvider.id());

				JsonObject numberProviderJson = NumberProviders.DIRECT_CODEC.encodeStart(ops, numberProvider.value()).getOrThrow(IllegalStateException::new).getAsJsonObject();
				futures.add(DataProvider.saveStable(output, numberProviderJson, getOutputPath(numberProvider)));
			}

			return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
		});
	}

	private Path getOutputPath(Holder numberProvider) {
		return this.pathProvider.json(numberProvider.id());
	}

	public record Holder(Identifier id, NumberProvider value) { }

	@Override
	public String getName() {
		return "Number Providers";
	}
}
