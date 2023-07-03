/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.api;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

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
     * @param dataVersion the current data version
     */
    public QuiltDataFixerBuilder(@Range(from = 0, to = Integer.MAX_VALUE) int dataVersion) {
        super(dataVersion);
        this.dataVersion = dataVersion;
    }

    /**
     * {@return the current data version}
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getDataVersion() {
        return this.dataVersion;
    }

    /**
     * Builds the final {@code DataFixer}.
     * <p>
     * This will build either an {@linkplain #buildUnoptimized() unoptimized fixer} or an
     * {@linkplain #buildOptimized(Set, Executor) optimized fixer}, depending on the vanilla game's settings.
     *
     * @param executorGetter the executor supplier, only invoked if the game is using optimized data fixers
     * @return the newly built data fixer
     */
    @Contract(value = "_, _ -> new")
    public @NotNull DataFixer build(Set<DSL.TypeReference> types, @NotNull Supplier<Executor> executorGetter) {
		return types.isEmpty() ? this.buildUnoptimized() : this.buildOptimized(types, executorGetter.get());
    }
}
