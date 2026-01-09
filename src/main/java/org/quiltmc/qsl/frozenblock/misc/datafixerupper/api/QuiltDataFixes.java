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

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.api;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.quiltmc.qsl.frozenblock.misc.datafixerupper.impl.QuiltDataFixesInternals;

/**
 * Provides methods to register custom {@link DataFixer}s.
 * <p>
 * Modified to work on Fabric
 */
@UtilityClass
public class QuiltDataFixes {

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
    public static void registerFixer(
		String modId,
		@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
		DataFixer dataFixer
	) {
        requireNonNull(modId, "modId cannot be null");
        //noinspection ConstantConditions
        checkArgument(currentVersion >= 0, "currentVersion must be positive");
        requireNonNull(dataFixer, "dataFixer cannot be null");

        if (isFrozen()) throw new IllegalStateException("Can't register data fixer after registry is frozen");
        QuiltDataFixesInternals.get().registerFixer(modId, currentVersion, dataFixer);
    }

    /**
     * Registers a new data fixer.
     *
     * @param mod The mod container
     * @param currentVersion The current version of the mod's data
     * @param dataFixer The data fixer
     */
    public static void registerFixer(
		ModContainer mod,
		@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
		DataFixer dataFixer
	) {
        requireNonNull(mod, "mod cannot be null");

        registerFixer(mod.getMetadata().getId(), currentVersion, dataFixer);
    }

    /**
     * Builds and registers a new data fixer.
     *
     * @param mod The mod container
     * @param dataFixerBuilder The data fixer builder
     */
    public static void buildAndRegisterFixer(
		ModContainer mod,
		QuiltDataFixerBuilder dataFixerBuilder
	) {
        requireNonNull(mod, "mod cannot be null");
        requireNonNull(dataFixerBuilder, "data fixer builder cannot be null");

        registerFixer(mod.getMetadata().getId(), dataFixerBuilder.getDataVersion(), buildFixer(dataFixerBuilder));
    }

	/**
	 * Registers a new data fixer for use with Minecraft version-specific datafixing.
	 *
	 * @param modId The mod identifier
	 * @param currentVersion The current version of the mod's data
	 * @param dataFixer The data fixer
	 */
	public static void registerMinecraftFixer(
		String modId,
		@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
		DataFixer dataFixer
	) {
		requireNonNull(modId, "modId cannot be null");
		//noinspection ConstantConditions
		checkArgument(currentVersion >= 0, "currentVersion must be positive");
		requireNonNull(dataFixer, "dataFixer cannot be null");

		if (isFrozen()) throw new IllegalStateException("Can't register data fixer after registry is frozen");
		QuiltDataFixesInternals.get().registerMinecraftFixer(modId, currentVersion, dataFixer);
	}

	/**
	 * Registers a new data fixer for use with Minecraft version-specific datafixing.
	 *
	 * @param mod The mod container
	 * @param currentVersion The current version of the mod's data
	 * @param dataFixer The data fixer
	 */
	public static void registerMinecraftFixer(
		ModContainer mod,
		@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
		DataFixer dataFixer
	) {
		requireNonNull(mod, "mod cannot be null");
		registerMinecraftFixer(mod.getMetadata().getId(), currentVersion, dataFixer);
	}

	/**
	 * Builds and registers a new data fixer for use with Minecraft version-specific datafixing.
	 *
	 * @param mod The mod container
	 * @param dataFixerBuilder The data fixer builder
	 */
	public static void buildAndRegisterMinecraftFixer(
		ModContainer mod,
		QuiltDataFixerBuilder dataFixerBuilder
	) {
		requireNonNull(mod, "mod cannot be null");
		requireNonNull(dataFixerBuilder, "data fixer builder cannot be null");

		registerMinecraftFixer(mod.getMetadata().getId(), dataFixerBuilder.getDataVersion(), buildFixer(dataFixerBuilder));
	}

	/**
	 * Builds a new data fixer.
	 *
	 * @param dataFixerBuilder The data fixer builder
	 * @return The built data fixer.
	 */
	public static DataFixer buildFixer(QuiltDataFixerBuilder dataFixerBuilder) {
		requireNonNull(dataFixerBuilder, "data fixer builder cannot be null");

		Supplier<Executor> executor = () -> Executors.newSingleThreadExecutor(
			new ThreadFactoryBuilder().setNameFormat("FrozenLib Quilt Datafixer Bootstrap").setDaemon(true).setPriority(1).build()
		);

		return dataFixerBuilder.build(DataFixTypes.TYPES_FOR_LEVEL_LIST, executor);
	}

    /**
     * Gets a mod's Minecraft version-specificdata fixer.
     *
     * @param modId The mod identifier
     * @return The mod's data fixer, or empty if the mod hasn't registered one
     */
    public static Optional<DataFixer> getFixer(String modId) {
        requireNonNull(modId, "modId cannot be null");

		final  QuiltDataFixesInternals.DataFixerEntry entry = QuiltDataFixesInternals.get().getFixerEntry(modId);
        if (entry == null) return Optional.empty();
        return Optional.of(entry.dataFixer());
    }

	/**
	 * Gets a mod's Minecraft version-specific data fixer.
	 *
	 * @param modId The mod identifier
	 * @return The mod's data fixer, or empty if the mod hasn't registered one
	 */
	public static Optional<DataFixer> getMinecraftFixer(String modId) {
		requireNonNull(modId, "modId cannot be null");

		final QuiltDataFixesInternals.DataFixerEntry entry = QuiltDataFixesInternals.get().getMinecraftFixerEntry(modId);
		if (entry == null) return Optional.empty();
		return Optional.of(entry.dataFixer());
	}

	/**
	 * Gets a mod's Minecraft version-specific data version from a {@link CompoundTag}.
	 *
	 * @param compound The compound
	 * @param modId The mod identifier
	 * @return The mod's data version, or {@code 0} if the compound has no data for that mod
	 */
	@Contract(pure = true)
	@Range(from = 0, to = Integer.MAX_VALUE)
	public static int getModDataVersion(CompoundTag compound, String modId) {
		requireNonNull(compound, "compound cannot be null");
		requireNonNull(modId, "modId cannot be null");

		return QuiltDataFixesInternals.getModDataVersion(compound, modId).orElse(0);
	}

    /**
     * Gets a mod's Minecraft version-specific data version from a {@link CompoundTag}.
     *
     * @param compound The compound
     * @param modId The mod identifier
     * @return The mod's data version, or {@code 0} if the compound has no data for that mod
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getModMinecraftDataVersion(CompoundTag compound, String modId) {
        requireNonNull(compound, "compound cannot be null");
        requireNonNull(modId, "modId cannot be null");

        return QuiltDataFixesInternals.getModMinecraftDataVersion(compound, modId).orElse(0);
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
