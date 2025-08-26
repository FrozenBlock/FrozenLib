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

package net.frozenblock.lib.block.client.render_type.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class BlockRenderTypeOverwriteManager implements SimpleResourceReloadListener<BlockRenderTypeOverwriteManager.RenderTypeLoader> {
	private static final Logger LOGGER = LoggerFactory.getLogger("Block Render Type Overwrite Manager");
	private static final String DIRECTORY = "block_render_type_overwrites";

	public static final BlockRenderTypeOverwriteManager INSTANCE = new BlockRenderTypeOverwriteManager();

	private final List<BlockRenderTypeOverwrite> overwrites = new ArrayList<>();

	public void addFinalizedOverwrite(BlockRenderTypeOverwrite overwrite) {
		this.overwrites.add(overwrite);
	}

	@Override
	public CompletableFuture<RenderTypeLoader> load(ResourceManager manager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> new RenderTypeLoader(manager), executor);
	}

	@Override
	public CompletableFuture<Void> apply(@NotNull BlockRenderTypeOverwriteManager.RenderTypeLoader prepared, ResourceManager manager, Executor executor) {
		this.overwrites.clear();
		prepared.getOverwrites().forEach(this::addFinalizedOverwrite);
		this.applyOverwrites();
		return CompletableFuture.runAsync(() -> {});
	}

	private void applyOverwrites() {
		BlockRenderLayerMap renderLayerRegistry = BlockRenderLayerMap.INSTANCE;

		this.overwrites.forEach(overwrite -> {
			renderLayerRegistry.putBlock(overwrite.getBlock(), overwrite.getRenderType());
		});
	}

	@NotNull
	public ResourceLocation getFabricId() {
		return FrozenLibConstants.id("block_render_type_overwrites");
	}

	public static class RenderTypeLoader {
		private final ResourceManager manager;
		private final List<BlockRenderTypeOverwrite> parsedOverwrites = new ArrayList<>();

		public RenderTypeLoader(ResourceManager manager) {
			this.manager = manager;
			this.loadRenderTypeOverwrites();
		}

		private void loadRenderTypeOverwrites() {
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
				LOGGER.error("Unable to open BufferedReader for file: `{}`", location);
				return;
			}

			JsonObject json = GsonHelper.parse(reader);
			DataResult<? extends Pair<? extends BlockRenderTypeOverwrite.RenderTypeOverwriteHolder, JsonElement>> dataResult
				= BlockRenderTypeOverwrite.RenderTypeOverwriteHolder.CODEC.decode(JsonOps.INSTANCE, json);

			dataResult.resultOrPartial((string) -> LOGGER.error("Failed to parse render type override for file: '{}'", location))
				.ifPresent(overwrite -> {
					ResourceLocation blockName = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().replace(".json", ""));
					LOGGER.info(blockName.toString());

					Block block = BuiltInRegistries.BLOCK.getOptional(blockName).orElse(null);
					if (block != null) {
						parsedOverwrites.add(new BlockRenderTypeOverwrite(block, overwrite.getFirst().renderTypeOverwrite()));
					} else {
						LOGGER.error("Failed to find block of name: '{}'", blockName);
					}
				});
		}

		public List<BlockRenderTypeOverwrite> getOverwrites() {
			return this.parsedOverwrites;
		}
	}
}
