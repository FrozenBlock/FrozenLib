/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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
import net.fabricmc.frozenblock.datafixer.api.SchemaRegistry;
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
	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);

		this.registeredBlockEntities = new SchemaRegistryImpl();
		FabricDataFixesInternals.registerBlockEntities(this.registeredBlockEntities, schema);

		ImmutableMap<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> modRegistry = this.registeredBlockEntities.get();

		for (Map.Entry<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> entry : modRegistry.entrySet()) {
			Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>> value = entry.getValue();

			value.ifLeft(supplier -> schema.register(map, entry.getKey(), supplier));

			value.ifRight(function -> schema.register(map, entry.getKey(), function));
		}

		return map;
	}

	@Override
	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);

		this.registeredEntities = new SchemaRegistryImpl();
		FabricDataFixesInternals.registerEntities(this.registeredEntities, schema);

		ImmutableMap<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> modRegistry = this.registeredEntities.get();

		for (Map.Entry<String, Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>>> entry : modRegistry.entrySet()) {
			Either<Supplier<TypeTemplate>, Function<String, TypeTemplate>> value = entry.getValue();

			value.ifLeft(supplier -> schema.register(map, entry.getKey(), supplier));

			value.ifRight(function -> schema.register(map, entry.getKey(), function));
		}

		return map;
	}
}
