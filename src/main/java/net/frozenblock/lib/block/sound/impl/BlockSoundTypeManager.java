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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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
import net.frozenblock.lib.block.sound.impl.overwrite.BlockArrayBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.overwrite.BlockBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.overwrite.ResourceLocationBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.overwrite.TagBlockSoundTypeOverwrite;
import net.minecraft.resources.ResourceLocation;
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
	private static final Logger LOGGER = LoggerFactory.getLogger("FrozenLib Block Sound Type Manager");
	private static final String DIRECTORY = "blocksoundoverwrites";

	public static final BlockSoundTypeManager INSTANCE = new BlockSoundTypeManager();

	private final List<AbstractBlockSoundTypeOverwrite<?>> builtInOverwrites = new ArrayList<>();
	private final List<AbstractBlockSoundTypeOverwrite<?>> overwrites = new ArrayList<>();

	public void addBuiltInOverwrite(Block block, SoundType soundType, BooleanSupplier condition) {
		this.builtInOverwrites.add(new BlockBlockSoundTypeOverwrite(block, soundType, condition));
	}

	public void addBuiltInOverwrite(Block[] blocks, SoundType soundType, BooleanSupplier condition) {
		this.builtInOverwrites.add(new BlockArrayBlockSoundTypeOverwrite(blocks, soundType, condition));
	}

	public void addBuiltInOverwrite(TagKey<Block> tagKey, SoundType soundType, BooleanSupplier condition) {
		this.builtInOverwrites.add(new TagBlockSoundTypeOverwrite(tagKey, soundType, condition));
	}

	public void addBuiltInOverwrite(ResourceLocation resourceLocation, SoundType soundType, BooleanSupplier condition) {
		this.builtInOverwrites.add(new ResourceLocationBlockSoundTypeOverwrite(resourceLocation, soundType, condition));
	}

	public void addFinalizedOverwrite(AbstractBlockSoundTypeOverwrite<?> overwrite) {
		this.overwrites.add(overwrite);
	}

	public Optional<SoundType> getSoundType(@NotNull BlockState state) {
		return this.getSoundType(state.getBlock());
	}

	public Optional<SoundType> getSoundType(Block block) {
		for (AbstractBlockSoundTypeOverwrite<?> overwrite : this.overwrites) {
			if (overwrite.getSoundCondition().getAsBoolean() && overwrite.matches(block)) {
				return Optional.of(overwrite.getSoundType());
			}
		}
		return Optional.empty();
	}

	public static @NotNull ResourceLocation getGeneratedPath(@NotNull ResourceLocation blockId) {
		return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), DIRECTORY + "/" + blockId.getPath() + ".json");
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
	public ResourceLocation getFabricId() {
		return FrozenLibConstants.id("block_sound_type_reloader");
	}

	public static class SoundTypeLoader {
		private final ResourceManager manager;
		private final List<AbstractBlockSoundTypeOverwrite<?>> parsedOverwrites = new ArrayList<>();

		public SoundTypeLoader(ResourceManager manager) {
			this.manager = manager;
			this.loadSoundOverwrites();
		}

		private void loadSoundOverwrites() {
			Map<ResourceLocation, Resource> resources = manager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
			var entrySet = resources.entrySet();
			for (Map.Entry<ResourceLocation, Resource> entry : entrySet) {
				this.addOverwrite(entry.getKey(), entry.getValue());
			}
		}

		private void addOverwrite(ResourceLocation location, @NotNull Resource resource) {
			BufferedReader reader;
			try {
				reader = resource.openAsReader();
			} catch (IOException e) {
				LOGGER.error(String.format("Unable to open BufferedReader for id %s", location), e);
				return;
			}

			JsonObject json = GsonHelper.parse(reader);
			Optional<Pair<? extends AbstractBlockSoundTypeOverwrite<?>, JsonElement>> result = Optional.empty();

			for (Codec<? extends AbstractBlockSoundTypeOverwrite<?>> codec : SoundTypeCodecs.possibleCodecs()) {
				DataResult<? extends Pair<? extends AbstractBlockSoundTypeOverwrite<?>, JsonElement>> possibleResult = codec.decode(JsonOps.INSTANCE, json);
				if (possibleResult.isSuccess()) {
					result = Optional.of(possibleResult.getOrThrow());
					break;
				}
			}

			if (result.isEmpty()) {
				LOGGER.error(String.format("Unable to parse sound overwrite file %s.", location));
				return;
			}

			this.parsedOverwrites.add(result.get().getFirst());
		}

		public List<AbstractBlockSoundTypeOverwrite<?>> getOverwrites() {
			return this.parsedOverwrites;
		}
	}
}
