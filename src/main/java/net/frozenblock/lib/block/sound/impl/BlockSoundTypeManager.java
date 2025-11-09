/*
 * Copyright (C) 2024-2025 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.block.sound.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.sound.api.SoundTypeCodecs;
import net.frozenblock.lib.block.sound.impl.overwrite.AbstractBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.overwrite.BlockStateBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.overwrite.HolderSetBlockSoundTypeOverwrite;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class BlockSoundTypeManager implements SimpleResourceReloadListener<BlockSoundTypeManager.SoundTypeLoader> {
	private static final Logger LOGGER = LoggerFactory.getLogger("Block Sound Type Overwrite Manager");
	private static final String DIRECTORY = "block_sound_overwrites";

	public static final BlockSoundTypeManager INSTANCE = new BlockSoundTypeManager();

	private final List<AbstractBlockSoundTypeOverwrite<?>> builtInOverwrites = new ArrayList<>();
	private final List<AbstractBlockSoundTypeOverwrite<?>> overwrites = new ArrayList<>();

	public void addBuiltInOverwrite(@NotNull BlockState blockState, SoundType soundType, BooleanSupplier condition) {
		this.builtInOverwrites.add(
			new BlockStateBlockSoundTypeOverwrite(
				blockState,
				soundType,
				condition
			)
		);
	}

	public void addBuiltInOverwrite(@NotNull Block block, SoundType soundType, BooleanSupplier condition) {
		this.builtInOverwrites.add(
			new HolderSetBlockSoundTypeOverwrite(
				HolderSet.direct(block.builtInRegistryHolder()),
				soundType,
				condition
			)
		);
	}

	public void addBuiltInOverwrite(Block[] blocks, SoundType soundType, BooleanSupplier condition) {
		List<Block> blockList = Arrays.stream(blocks).toList();

		if (!blockList.isEmpty()) {
			this.builtInOverwrites.add(
				new HolderSetBlockSoundTypeOverwrite(
					HolderSet.direct(
						Block::builtInRegistryHolder,
						Arrays.stream(blocks).toList()
					),
					soundType,
					condition
				)
			);
		}
	}

	public void addBuiltInOverwrite(TagKey<Block> tagKey, SoundType soundType, BooleanSupplier condition) {
		this.builtInOverwrites.add(
			new HolderSetBlockSoundTypeOverwrite(
				new HolderSet.Named<>(
					BuiltInRegistries.BLOCK,
					tagKey
				),
				soundType,
				condition
			)
		);
	}

	public void addBuiltInOverwrite(Identifier identifier, SoundType soundType, BooleanSupplier condition) {
		Optional<Block> optionalBlock = BuiltInRegistries.BLOCK.getOptional(identifier);
		optionalBlock.ifPresent(block -> this.builtInOverwrites.add(
			new HolderSetBlockSoundTypeOverwrite(
				HolderSet.direct(block.builtInRegistryHolder()),
				soundType,
				condition
			)
		));
	}

	public void addFinalizedOverwrite(AbstractBlockSoundTypeOverwrite<?> overwrite) {
		this.overwrites.add(overwrite);
	}

	public Optional<SoundType> getSoundType(@NotNull BlockState state) {
		for (AbstractBlockSoundTypeOverwrite<?> overwrite : this.overwrites) {
			if (overwrite.getSoundCondition().getAsBoolean() && overwrite.matches(state)) {
				return Optional.of(overwrite.getSoundType());
			}
		}
		return Optional.empty();
	}

	public static @NotNull Identifier getGeneratedPath(@NotNull Identifier blockId) {
		return Identifier.fromNamespaceAndPath(blockId.getNamespace(), DIRECTORY + "/" + blockId.getPath() + ".json");
	}

	@Override
	public CompletableFuture<SoundTypeLoader> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> new SoundTypeLoader(manager), executor);
	}

	@Override
	public CompletableFuture<Void> apply(@NotNull SoundTypeLoader prepared, ResourceManager manager, Executor executor) {
		this.overwrites.clear();
		// Load data-driven sounds first for player satisfaction.
		prepared.getOverwrites().forEach(this::addFinalizedOverwrite);
		// Load our queued overwrites.
		this.builtInOverwrites.forEach(this::addFinalizedOverwrite);
		return CompletableFuture.runAsync(() -> {
		});
	}

	@NotNull
	public Identifier getFabricId() {
		return FrozenLibConstants.id("block_sound_type_overwrites");
	}

	public static class SoundTypeLoader {
		private final ResourceManager manager;
		private final List<AbstractBlockSoundTypeOverwrite<?>> parsedOverwrites = new ArrayList<>();

		public SoundTypeLoader(ResourceManager manager) {
			this.manager = manager;
			this.loadSoundOverwrites();
		}

		private void loadSoundOverwrites() {
			Map<Identifier, Resource> resources = manager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
			var entrySet = resources.entrySet();
			for (Map.Entry<Identifier, Resource> entry : entrySet) {
				this.addOverwrite(entry.getKey(), entry.getValue());
			}
		}

		private void addOverwrite(Identifier location, @NotNull Resource resource) {
			BufferedReader reader;
			try {
				reader = resource.openAsReader();
			} catch (IOException e) {
				LOGGER.error("Unable to open BufferedReader for file: `{}`", location);
				return;
			}

			JsonObject json = GsonHelper.parse(reader);
			DataResult<? extends Pair<? extends AbstractBlockSoundTypeOverwrite<?>, JsonElement>> dataResult
				= SoundTypeCodecs.HOLDER_SET_BLOCK_SOUND_TYPE_OVERWRITE_CODEC.decode(JsonOps.INSTANCE, json);

			dataResult.resultOrPartial((string) -> LOGGER.error("Failed to parse sound override for file: '{}'", location))
				.ifPresent(overwrite -> parsedOverwrites.add(overwrite.getFirst()));
		}

		public List<AbstractBlockSoundTypeOverwrite<?>> getOverwrites() {
			return this.parsedOverwrites;
		}
	}
}
