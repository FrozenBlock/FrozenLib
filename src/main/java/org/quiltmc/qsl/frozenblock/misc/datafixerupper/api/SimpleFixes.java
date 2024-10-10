/*
 * Copyright 2024 The Quilt Project
 * Copyright 2024 FrozenBlock
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

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import net.frozenblock.lib.datafix.api.BlockStateRenameFix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.fixes.NamespacedTypeRenameFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.fixes.SimplestEntityRenameFix;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.jetbrains.annotations.NotNull;

/**
 * Provides methods to add common {@link DataFix}es to {@link DataFixerBuilder}s.
 * <p>
 * Modified to work on Fabric
 */
public final class SimpleFixes {
    private SimpleFixes() {
        throw new RuntimeException("SimpleFixes contains only static declarations.");
    }

    /**
     * Adds a block rename fix to the builder, in case a block's identifier is changed.
     *
     * @param builder the builder
     * @param name    the fix's name
     * @param oldId   the block's old identifier
     * @param newId   the block's new identifier
     * @param schema  the schema this fixer should be a part of
     * @see BlockRenameFix
     */
    public static void addBlockRenameFix(@NotNull DataFixerBuilder builder, @NotNull String name,
                                         @NotNull ResourceLocation oldId, @NotNull ResourceLocation newId,
                                         @NotNull Schema schema) {
        requireNonNull(builder, "DataFixerBuilder cannot be null");
        requireNonNull(name, "Fix name cannot be null");
        requireNonNull(oldId, "Old identifier cannot be null");
        requireNonNull(newId, "New identifier cannot be null");
        requireNonNull(schema, "Schema cannot be null");

        final String oldIdStr = oldId.toString(), newIdStr = newId.toString();
        builder.addFixer(BlockRenameFix.create(schema, name, (inputName) ->
                Objects.equals(NamespacedSchema.ensureNamespaced(inputName), oldIdStr) ? newIdStr : inputName));
    }

	/**
	 * Adds an entity rename fix to the builder, in case an entity's identifier is changed.
	 *
	 * @param builder the builder
	 * @param name    the fix's name
	 * @param oldId   the entity's old identifier
	 * @param newId   the entity's new identifier
	 * @param schema  the schema this fix should be a part of
	 * @see SimplestEntityRenameFix
	 */
	public static void addEntityRenameFix(@NotNull DataFixerBuilder builder, @NotNull String name,
										  @NotNull ResourceLocation oldId, @NotNull ResourceLocation newId,
										  @NotNull Schema schema) {
		requireNonNull(builder, "DataFixerBuilder cannot be null");
		requireNonNull(name, "Fix name cannot be null");
		requireNonNull(oldId, "Old identifier cannot be null");
		requireNonNull(newId, "New identifier cannot be null");
		requireNonNull(schema, "Schema cannot be null");

		final String oldIdStr = oldId.toString(), newIdStr = newId.toString();
		builder.addFixer(new SimplestEntityRenameFix(name, schema, false) {
			@Override
			protected String rename(String inputName) {
				return Objects.equals(NamespacedSchema.ensureNamespaced(inputName), oldIdStr) ? newIdStr : inputName;
			}
		});
	}

    /**
     * Adds an item rename fix to the builder, in case an item's identifier is changed.
     *
     * @param builder the builder
     * @param name    the fix's name
     * @param oldId   the item's old identifier
     * @param newId   the item's new identifier
     * @param schema  the schema this fix should be a part of
     * @see ItemRenameFix
     */
    public static void addItemRenameFix(@NotNull DataFixerBuilder builder, @NotNull String name,
                                        @NotNull ResourceLocation oldId, @NotNull ResourceLocation newId,
                                        @NotNull Schema schema) {
        requireNonNull(builder, "DataFixerBuilder cannot be null");
        requireNonNull(name, "Fix name cannot be null");
        requireNonNull(oldId, "Old identifier cannot be null");
        requireNonNull(newId, "New identifier cannot be null");
        requireNonNull(schema, "Schema cannot be null");

        final String oldIdStr = oldId.toString(), newIdStr = newId.toString();
        builder.addFixer(ItemRenameFix.create(schema, name, (inputName) ->
                Objects.equals(NamespacedSchema.ensureNamespaced(inputName), oldIdStr) ? newIdStr : inputName));
    }

    /**
     * Adds a blockstate rename fix to the builder, in case a blockstate's name is changed.
     *
     * @param builder       the builder
     * @param name          the fix's name
     * @param blockId       the block's identifier
     * @param oldState      the blockstate's old name
     * @param defaultValue  the blockstate's default value
     * @param newState      the blockstates's new name
     * @param schema        the schema this fixer should be a part of
     * @see BlockStateRenameFix
     */
    public static void addBlockStateRenameFix(@NotNull DataFixerBuilder builder, @NotNull String name,
                                         @NotNull ResourceLocation blockId, @NotNull String oldState,
                                         @NotNull String defaultValue, @NotNull String newState,
                                         @NotNull Schema schema) {
        requireNonNull(builder, "DataFixerBuilder cannot be null");
        requireNonNull(name, "Fix name cannot be null");
        requireNonNull(blockId, "Block Id cannot be null");
        requireNonNull(oldState, "Old BlockState cannot be null");
        requireNonNull(defaultValue, "Default value cannot be null");
        requireNonNull(newState, "New BlockState cannot be null");
        requireNonNull(schema, "Schema cannot be null");

        final String blockIdStr = blockId.toString();
        builder.addFixer(new BlockStateRenameFix(schema, name, blockIdStr, oldState, defaultValue, newState));
    }

    /**
     * Adds a biome rename fix to the builder, in case biome identifiers are changed.
     *
     * @param builder the builder
     * @param name    the fix's name
     * @param changes a map of old biome identifiers to new biome identifiers
     * @param schema  the schema this fixer should be a part of
     * @see NamespacedTypeRenameFix
     */
    public static void addBiomeRenameFix(@NotNull DataFixerBuilder builder, @NotNull String name,
                                         @NotNull Map<ResourceLocation, ResourceLocation> changes,
                                         @NotNull Schema schema) {
        requireNonNull(builder, "DataFixerBuilder cannot be null");
        requireNonNull(name, "Fix name cannot be null");
        requireNonNull(changes, "Changes cannot be null");
        requireNonNull(schema, "Schema cannot be null");

        var mapBuilder = ImmutableMap.<String, String>builder();
        for (var entry : changes.entrySet()) {
            mapBuilder.put(entry.getKey().toString(), entry.getValue().toString());
        }
        builder.addFixer(new NamespacedTypeRenameFix(schema, name, References.BIOME, DataFixers.createRenamer(mapBuilder.build())));
    }
}
