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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Provides methods to register custom {@link DataFixer}s.
 * <p>
 * Modified to work on Fabric
 */
public final class QuiltDataFixes {
    private QuiltDataFixes() {
        throw new UnsupportedOperationException("QuiltDataFixes contains only static declarations.");
    }

    /**
     * A "base" version {@code 0} schema, for use by all mods.
     * <p>
     * This schema <em>must</em> be the first one added!
     *
     * @see DataFixerBuilder#addSchema(int, BiFunction)
     */
    public static final BiFunction<Integer, Schema, Schema> BASE_SCHEMA = (version, parent) -> {
        checkArgument(version == 0, "version must be 0");
        checkArgument(parent == null, "parent must be null");
        return QuiltDataFixesInternals.get().createBaseSchema();
    };

    /**
     * Registers a new data fixer.
     *
     * @param modId          the mod identifier
     * @param currentVersion the current version of the mod's data
     * @param dataFixer      the data fixer
     */
    public static void registerFixer(@NotNull String modId,
                                     @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
                                     @NotNull DataFixer dataFixer) {
        requireNonNull(modId, "modId cannot be null");
        //noinspection ConstantConditions
        checkArgument(currentVersion >= 0, "currentVersion must be positive");
        requireNonNull(dataFixer, "dataFixer cannot be null");

        if (isFrozen()) {
            throw new IllegalStateException("Can't register data fixer after registry is frozen");
        }

        QuiltDataFixesInternals.get().registerFixer(modId, currentVersion, dataFixer);
    }

    /**
     * Registers a new data fixer.
     *
     * @param mod            the mod container
     * @param currentVersion the current version of the mod's data
     * @param dataFixer      the data fixer
     */
    public static void registerFixer(@NotNull ModContainer mod,
                                     @Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
                                     @NotNull DataFixer dataFixer) {
        requireNonNull(mod, "mod cannot be null");

        registerFixer(mod.getMetadata().getId(), currentVersion, dataFixer);
    }

    /**
     * Builds and registers a new data fixer.
     *
     * @param mod              the mod container
     * @param dataFixerBuilder the data fixer builder
     */
    public static void buildAndRegisterFixer(@NotNull ModContainer mod,
                                             @NotNull QuiltDataFixerBuilder dataFixerBuilder) {
        requireNonNull(mod, "mod cannot be null");
        requireNonNull(dataFixerBuilder, "data fixer builder cannot be null");

		Supplier<Executor> executor = () -> Executors.newSingleThreadExecutor(
				new ThreadFactoryBuilder().setNameFormat("FrozenLib Quilt Datafixer Bootstrap").setDaemon(true).setPriority(1).build()
		);

        registerFixer(mod.getMetadata().getId(), dataFixerBuilder.getDataVersion(),
                dataFixerBuilder.build(SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE, executor));
    }

    /**
     * Gets a mod's data fixer.
     *
     * @param modId the mod identifier
     * @return the mod's data fixer, or empty if the mod hasn't registered one
     */
    public static @NotNull Optional<DataFixer> getFixer(@NotNull String modId) {
        requireNonNull(modId, "modId cannot be null");

        QuiltDataFixesInternals.DataFixerEntry entry = QuiltDataFixesInternals.get().getFixerEntry(modId);
        if (entry == null) {
            return Optional.empty();
        }
        return Optional.of(entry.dataFixer());
    }

    /**
     * Gets a mod's data version from a {@link CompoundTag}.
     *
     * @param compound the compound
     * @param modId    the mod identifier
     * @return the mod's data version, or {@code 0} if the compound has no data for that mod
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getModDataVersion(@NotNull CompoundTag compound, @NotNull String modId) {
        requireNonNull(compound, "compound cannot be null");
        requireNonNull(modId, "modId cannot be null");

        return QuiltDataFixesInternals.getModDataVersion(compound, modId);
    }

    /**
     * Checks if the data fixer registry is frozen.
     *
     * @return {@code true} if frozen, or {@code false} otherwise.
     */
    @Contract(pure = true)
    public static boolean isFrozen() {
        return QuiltDataFixesInternals.get().isFrozen();
    }
}
