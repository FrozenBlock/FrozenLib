/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.frozenblock.datafixer.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import net.fabricmc.frozenblock.datafixer.api.DataFixerEntrypoint;
import net.fabricmc.frozenblock.datafixer.api.SchemaRegistry;
import net.frozenblock.lib.entrypoint.api.EntrypointHelper;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class FabricSubSchema extends NamespacedSchema {
	public SchemaRegistry registeredBlockEntities;
	public SchemaRegistry registeredEntities;

	public FabricSubSchema(int versionKey, Schema parent) {
		super(versionKey, parent);
	}

	@Override
	public synchronized Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

		if (this.registeredBlockEntities == null) {
			final SchemaRegistryImpl registry = new SchemaRegistryImpl();
			EntrypointHelper.forEachEntrypoint(DataFixerEntrypoint.class, entrypoint -> entrypoint.onRegisterBlockEntities(registry, this));
			this.registeredBlockEntities = registry;
		}

		applyRegistry(this.registeredBlockEntities.get(), map);
		return map;
	}

	@Override
	public synchronized Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

		if (this.registeredEntities == null) {
			final SchemaRegistryImpl registry = new SchemaRegistryImpl();
			EntrypointHelper.forEachEntrypoint(DataFixerEntrypoint.class, entrypoint -> entrypoint.onRegisterEntities(registry, this));
			this.registeredEntities = registry;
		}

		applyRegistry(this.registeredEntities.get(), map);
		return map;
	}

	private static void applyRegistry(
		ImmutableMap<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> registry,
		Map<String, Supplier<TypeTemplate>> map
	) {
		for (Map.Entry<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> entry : registry.entrySet()) {
			final Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>> value = entry.getValue();

			value.ifLeft(supplier -> map.put(entry.getKey(), supplier));
			value.ifRight(function -> map.put(entry.getKey(), () -> function.apply(entry.getKey())));
		}
	}
}
