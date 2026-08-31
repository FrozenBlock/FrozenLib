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

package net.fabricmc.frozenblock.datafixer.api;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import net.fabricmc.frozenblock.datafixer.impl.FabricDataFixesInternals;

/**
 * Provides methods to register custom {@link DataFixer}s.
 * <p>
 * Modified to work on Fabric
 */
@UtilityClass
public class FabricDataFixes {

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
        return FabricDataFixesInternals.get().createBaseSchema();
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
        registerFixer(modId, currentVersion, null, dataFixer);
    }

	/**
	 * Registers a new data fixer.
	 * <p>
	 * The optional {@code key} lets a single mod register more than one data fixer - for
	 * example, one for its own data and another for a separate concern - each tracked under
	 * its own saved data version.
	 *
	 * @param modId          the mod identifier
	 * @param currentVersion the current version of the mod's data
	 * @param key            the optional key of the saved current version
	 * @param dataFixer      the data fixer
	 */
	public static void registerFixer(
		String modId,
		@Range(from = 0, to = Integer.MAX_VALUE) int currentVersion,
		@Nullable String key,
		DataFixer dataFixer
	) {
		requireNonNull(modId, "modId cannot be null");
		//noinspection ConstantConditions
		checkArgument(currentVersion >= 0, "currentVersion must be positive");
		requireNonNull(dataFixer, "dataFixer cannot be null");

		if (isFrozen()) throw new IllegalStateException("Can't register data fixer after registry is frozen");
		FabricDataFixesInternals.get().registerFixer(modId, currentVersion, key, dataFixer);
	}

	/**
	 * Builds and registers a new data fixer.
	 *
	 * @param modId The mod identifier
	 * @param builder The data fixer builder
	 */
	public static void buildAndRegisterFixer(
		String modId,
		FabricDataFixerBuilder builder
	) {
		buildAndRegisterFixer(modId, null, builder);
	}

	/**
	 * Builds and registers a new data fixer.
	 *
	 * @param modId The mod identifier
	 * @param key The optional key of the saved current version
	 * @param builder The data fixer builder
	 * @see #registerFixer(String, int, String, DataFixer)
	 */
	public static void buildAndRegisterFixer(String modId, @Nullable String key, FabricDataFixerBuilder builder) {
		requireNonNull(modId, "modId cannot be null");
		requireNonNull(builder, "data fixer builder cannot be null");

		registerFixer(modId, builder.getDataVersion(), key, buildFixer(builder));
	}

	/**
	 * Builds a new data fixer.
	 *
	 * @param builder The data fixer builder
	 * @return The built data fixer.
	 */
	public static DataFixer buildFixer(FabricDataFixerBuilder builder) {
		requireNonNull(builder, "data fixer builder cannot be null");

		Supplier<Executor> executor = () -> Executors.newSingleThreadExecutor(
			new ThreadFactoryBuilder().setNameFormat("FrozenLib Fabric Datafixer Bootstrap").setDaemon(true).setPriority(1).build()
		);

		return builder.build(DataFixTypes.TYPES_FOR_LEVEL_LIST, executor);
	}

    /**
     * Gets a mod's data fixer.
     *
     * @param modId The mod identifier
     * @return The mod's data fixer, or empty if the mod hasn't registered one
     */
    public static Optional<DataFixer> getFixer(String modId) {
        return getFixer(modId, null);
    }

	/**
	 * Gets a mod's data fixer registered under the given key.
	 *
	 * @param modId The mod identifier
	 * @param key The optional key the fixer was registered with
	 * @return The mod's data fixer, or empty if the mod hasn't registered one under that key
	 * @see #registerFixer(String, int, String, DataFixer)
	 */
	public static Optional<DataFixer> getFixer(String modId, @Nullable String key) {
		requireNonNull(modId, "modId cannot be null");

		final FabricDataFixesInternals.DataFixerEntry entry = FabricDataFixesInternals.get().getFixerEntry(modId, key);
		if (entry == null) return Optional.empty();
		return Optional.of(entry.dataFixer());
	}

	/**
	 * Gets a mod's data version from a {@link Dynamic}.
	 *
	 * @param dynamic The dynamic
	 * @param modId The mod identifier
	 * @return The mod's data version, or {@code 0} if the dynamic has no data for that mod
	 */
	@Contract(pure = true)
	@Range(from = 0, to = Integer.MAX_VALUE)
	public static int getModDataVersion(Dynamic<?> dynamic, String modId) {
		return getModDataVersion(dynamic, modId, null);
	}

    /**
     * Gets a mod's data version from a {@link Dynamic}, for a fixer registered under the given key.
     *
     * @param dynamic The dynamic
     * @param modId The mod identifier
     * @param key The optional key the fixer was registered with
     * @return The mod's data version, or {@code 0} if the dynamic has no data for that mod under that key
     * @see #registerFixer(String, int, String, DataFixer)
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public static int getModDataVersion(Dynamic<?> dynamic, String modId, @Nullable String key) {
        requireNonNull(dynamic, "dynamic cannot be null");
        requireNonNull(modId, "modId cannot be null");

        return FabricDataFixesInternals.getModDataVersion(dynamic, modId, key).orElse(0);
    }

    /**
     * Checks if the data fixer registry is frozen.
     *
     * @return {@code true} if frozen, or {@code false} otherwise.
     */
    @Contract(pure = true)
    public static boolean isFrozen() {
        return FabricDataFixesInternals.get().isFrozen();
    }
}
