/*
 * Copyright (C) 2024 FrozenBlock
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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.block.sound.api.SoundTypeCodecs;
import net.frozenblock.lib.block.sound.impl.queued.AbstractQueuedBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.queued.QueuedBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.queued.QueuedResourceLocationBlockSoundTypeOverwrite;
import net.frozenblock.lib.block.sound.impl.queued.QueuedTagBlockSoundTypeOverwrite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class BlockSoundTypeManager implements SimpleResourceReloadListener<BlockSoundTypeManager.SoundGroupLoader> {
	private static final Logger LOGGER = LoggerFactory.getLogger("FrozenLib Block Sound Group Manager");
	private static final String DIRECTORY = "blocksoundoverwrites";

	public static final BlockSoundTypeManager INSTANCE = new BlockSoundTypeManager();

	private final List<AbstractQueuedBlockSoundTypeOverwrite<?>> queuedOverwrites = new ArrayList<>();
	private final Map<ResourceLocation, List<BlockSoundTypeOverwrite>> overwrites = new Object2ObjectOpenHashMap<>();

	public void queueOverwrite(Block block, SoundType soundType, BooleanSupplier condition) {
		this.queuedOverwrites.add(new QueuedBlockSoundTypeOverwrite(block, soundType, condition));
	}

	public void queueOverwrite(TagKey<Block> tagKey, SoundType soundType, BooleanSupplier condition) {
		this.queuedOverwrites.add(new QueuedTagBlockSoundTypeOverwrite(tagKey, soundType, condition));
	}

	public void queueOverwrite(ResourceLocation resourceLocation, SoundType soundType, BooleanSupplier condition) {
		this.queuedOverwrites.add(new QueuedResourceLocationBlockSoundTypeOverwrite(resourceLocation, soundType, condition));
	}

	public void addOverwrite(ResourceLocation resourceLocation, BlockSoundTypeOverwrite overwrite) {
		List<BlockSoundTypeOverwrite> list = this.overwrites.getOrDefault(resourceLocation, new ArrayList<>());
		list.add(overwrite);
		this.overwrites.put(resourceLocation, list);
	}

	public Optional<SoundType> getSoundType(@NotNull BlockState state) {
		return this.getSoundType(state.getBlock());
	}

	public Optional<SoundType> getSoundType(Block block) {
		ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);
		if (!resourceLocation.equals(BuiltInRegistries.BLOCK.getDefaultKey())) {
			return this.getSoundType(resourceLocation);
		}
		return Optional.empty();
	}

	public Optional<SoundType> getSoundType(ResourceLocation resourceLocation) {
		if (this.overwrites.containsKey(resourceLocation)) {
			return this.getFirstEnabled(this.overwrites.get(resourceLocation));
		}
		return Optional.empty();
	}

	private Optional<SoundType> getFirstEnabled(@NotNull List<BlockSoundTypeOverwrite> overwrites) {
		for (BlockSoundTypeOverwrite overwrite : overwrites) {
			if (overwrite.condition().getAsBoolean()) {
				return Optional.of(overwrite.soundOverwrite());
			}
		}
		return Optional.empty();
	}

	public static @NotNull ResourceLocation getGeneratedPath(@NotNull ResourceLocation blockId) {
		return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), DIRECTORY + "/" + blockId.getPath() + ".json");
	}

	@Override
	public CompletableFuture<SoundGroupLoader> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> new SoundGroupLoader(manager, profiler), executor);
	}

	@Override
	public CompletableFuture<Void> apply(@NotNull SoundGroupLoader prepared, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
		this.overwrites.clear();
		// Load data-driven sounds first for player satisfaction.
		prepared.getOverwrites().forEach((resourceLocation, overwrite) -> {
			this.addOverwrite(overwrite.blockId(), overwrite);
		});
		// Load our queued overwrites.
		this.queuedOverwrites.forEach(queuedOverwrite -> {
			queuedOverwrite.accept((resourceLocation, self) -> {
				this.addOverwrite(resourceLocation, new BlockSoundTypeOverwrite(resourceLocation, self.getSoundType(), self.getSoundCondition()));
			});
		});
		return CompletableFuture.runAsync(() -> {});
	}

	@NotNull
	public ResourceLocation getFabricId() {
		return FrozenSharedConstants.id("block_sound_group_reloader");
	}

	public static class SoundGroupLoader {
		private final ResourceManager manager;
		private final ProfilerFiller profiler;
		private final Map<ResourceLocation, BlockSoundTypeOverwrite> parsedOverwrites = new Object2ObjectOpenHashMap<>();

		public SoundGroupLoader(ResourceManager manager, ProfilerFiller profiler) {
			this.manager = manager;
			this.profiler = profiler;
			this.loadSoundOverwrites();
		}

		private void loadSoundOverwrites() {
			this.profiler.push("Load Sound Overwrites");
			Map<ResourceLocation, Resource> resources = manager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
			var entrySet = resources.entrySet();
			for (Map.Entry<ResourceLocation, Resource> entry : entrySet) {
				this.addOverwrite(entry.getKey(), entry.getValue());
			}
			this.profiler.pop();
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
			DataResult<Pair<BlockSoundTypeOverwrite, JsonElement>> result = SoundTypeCodecs.SOUND_TYPE_OVERWRITE.decode(JsonOps.INSTANCE, json);

			if (result.error().isPresent()) {
				LOGGER.error(String.format("Unable to parse sound overwrite file %s. \nReason: %s", location, result.error().get().message()));
				return;
			}

			ResourceLocation overwriteId = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().substring((DIRECTORY + "/").length()));
			this.parsedOverwrites.put(overwriteId, result.result().orElseThrow().getFirst());
		}

		public Map<ResourceLocation, BlockSoundTypeOverwrite> getOverwrites() {
			return this.parsedOverwrites;
		}
	}
}
