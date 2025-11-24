/*
 * Copyright 2024-2025 The Quilt Project
 * Copyright 2024-2025 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.api;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

/**
 * An extended variant of the {@link DataFixerBuilder} class, which provides an extra method.
 * <p>
 * Modified to work on Fabric
 */
public class QuiltDataFixerBuilder extends DataFixerBuilder {
    protected final int dataVersion;

    /**
     * Creates a new {@code QuiltDataFixerBuilder}.
     *
     * @param dataVersion The current data version
     */
    public QuiltDataFixerBuilder(@Range(from = 0, to = Integer.MAX_VALUE) int dataVersion) {
        super(dataVersion);
        this.dataVersion = dataVersion;
    }

    /**
     * {@return Rhe current data version}
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getDataVersion() {
        return this.dataVersion;
    }

    /**
     * Builds the final {@code DataFixer}.
     * <p>
     * This will build either an {@linkplain #build() unoptimized fixer} or an
     * {@linkplain #build(Set, Supplier) optimized fixer}, depending on the vanilla game's settings.
     *
     * @param executorGetter The executor supplier, only invoked if the game is using optimized data fixers
     * @return The newly built data fixer
     */
    @Contract(value = "_, _ -> new")
    public DataFixer build(Set<DSL.TypeReference> types, Supplier<Executor> executorGetter) {
		return types.isEmpty() ? this.build().fixer() : Util.make(() -> {
			final var result = this.build();
			result.optimize(types, executorGetter.get()).join();
			return result.fixer();
		});
    }
}
