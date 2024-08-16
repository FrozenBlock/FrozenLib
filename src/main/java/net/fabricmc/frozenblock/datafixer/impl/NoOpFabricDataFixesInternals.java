/*
 * Copyright (c) 2016-2022 FabricMC
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
 *
 * This file is a modified version of Quilt Standard Libraries,
 * authored by QuiltMC.
 */

package net.fabricmc.frozenblock.datafixer.impl;

import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.fabricmc.frozenblock.datafixer.api.EmptySchema;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public final class NoOpFabricDataFixesInternals extends FabricDataFixesInternals {
	// From QSL.
	private final Schema schema = new EmptySchema(0);

	private boolean frozen = false;

	public NoOpFabricDataFixesInternals() {
	}

	@Override
	public void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @Nullable String key, DataFixerUpper dataFixer) {
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public @Nullable List<DataFixerEntry> getFixerEntries(String modId) {
		return null;
	}

	@Override
	public Schema getBaseSchema() {
		return this.schema;
	}

	@Override
	public Dynamic<Tag> updateWithAllFixers(DataFixTypes dataFixTypes, Dynamic<Tag> dynamic) {
		return new Dynamic<>(dynamic.getOps(), dynamic.getValue().copy());
	}

	@Override
	public CompoundTag addModDataVersions(CompoundTag nbt) {
		return nbt;
	}

	@Override
	public void freeze() {
		this.frozen = true;
	}

	@Override
	public boolean isFrozen() {
		return this.frozen;
	}
}