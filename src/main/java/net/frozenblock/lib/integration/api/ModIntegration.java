/*
 * Copyright 2023 The Quilt Project
 * Copyright 2023 FrozenBlock
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.integration.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public abstract class ModIntegration {

    private final String modID;
	private final String modRegistryID;
	private final boolean isModLoaded;

	public ModIntegration(String modID, String modRegistryID) {
		this.modID = modID;
		this.modRegistryID = modRegistryID;
		this.isModLoaded = FabricLoader.getInstance().isModLoaded(this.modID);
	}

    public ModIntegration(String modID) {
        this(modID, modID);
    }

    public String getID() {
        return this.modID;
    }

    public ResourceLocation id(String path) {
        return new ResourceLocation(this.modRegistryID, path);
    }

    public Block getBlock(String path) {
        return BuiltInRegistries.BLOCK.get(id(path));
    }

    public Item getItem(String path) {
        return BuiltInRegistries.ITEM.get(id(path));
    }

    public ResourceKey<Biome> getBiomeKey(String path) {
        return ResourceKey.create(Registries.BIOME, id(path));
    }

    public TagKey<Block> getBlockTag(String path) {
        var key = id(path);
        var registry = BuiltInRegistries.BLOCK;
        return getTag(registry, key);
    }

    public TagKey<Item> getItemTag(String path) {
        var key = id(path);
        var registry = BuiltInRegistries.ITEM;
        return getTag(registry, key);
    }

    public TagKey<Biome> getBiomeTag(String path) {
        var key = id(path);
        var registry = VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME);
        return getTag(registry, key);
    }

    public <T> TagKey<T> getTag(Registry<T> registry, ResourceLocation key) {
        return registry.getTagNames()
                .filter(tag -> tag.location().equals(key))
                .findAny()
                .orElse(TagKey.create(registry.key(), key));
    }

    @SuppressWarnings("unchecked")
    public <T> TagKey<T> getTag(HolderLookup.RegistryLookup<T> lookup, ResourceLocation key) {
        return lookup.listTagIds()
                .filter(tag -> tag.location().equals(key))
                .findAny()
                .orElse(TagKey.create((ResourceKey<Registry<T>>) lookup.key(), key));
    }

    public boolean modLoaded() {
        return this.isModLoaded;
    }

	/**
	 * Runs prior to registries freezing in order to allow for the registering of things.
	 */
	public void initPreFreeze() {
	}

	/**
	 * Runs prior to registries freezing in order to allow for registering things.
	 */
	@Environment(EnvType.CLIENT)
	public void clientInitPreFreeze() {
	}

    public abstract void init();

	@Environment(EnvType.CLIENT)
	public void clientInit() {
	}
}
