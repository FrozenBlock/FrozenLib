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

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import net.fabricmc.frozenblock.datafixer.api.EmptySchema;

/**
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public final class NoOpFabricDataFixesInternals extends FabricDataFixesInternals {
    private final Schema schema;

    private boolean frozen;

    public NoOpFabricDataFixesInternals() {
        this.schema = new EmptySchema(0);

        this.frozen = false;
    }

    @Override
    public void registerFixer(String modId, @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion, @Nullable String key, DataFixer dataFixer) {}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	@Nullable
    public DataFixerEntry getFixerEntry(String modId, @Nullable String key) {
        return null;
    }

	@Override
    public Schema createBaseSchema() {
        return this.schema;
    }

	@Override
	public void forEachFixer(Dynamic<?> dynamic, BiFunction<String, Integer, Integer> function) {}

	@Override
	public <T> Dynamic<T> updateWithAllFixers(DSL.TypeReference type, Dynamic<T> current, Optional<Map<String, Integer>> moddedDataVersions) {
		return new Dynamic<>(current.getOps(), current.getValue());
	}

    @Override
    public CompoundTag addModDataVersions(CompoundTag tag) {
        return tag;
    }

	@Override
	public Dynamic<?> addModDataVersions(Dynamic<?> dynamic) {
		return dynamic;
	}

	@Override
	public void addModDataVersions(ValueOutput output) {}

	@Override
    public void freeze() {
        this.frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return this.frozen;
    }
}
