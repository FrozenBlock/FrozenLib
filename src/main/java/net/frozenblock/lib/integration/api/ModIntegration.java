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

    public abstract void init();

	@Environment(EnvType.CLIENT)
	public void clientInit() {
	}
}
