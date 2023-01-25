package net.frozenblock.lib.integration.api;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public abstract class ModIntegration {

    private final String modID;
	private final String modRegistryID;

	public ModIntegration(String modID, String modRegistryID) {
		this.modID = modID;
		this.modRegistryID = modRegistryID;
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
        return Registry.BLOCK.get(id(path));
    }

    public Item getItem(String path) {
        return Registry.ITEM.get(id(path));
    }

    public ResourceKey<Biome> getBiomeKey(String path) {
        return ResourceKey.create(Registry.BIOME_REGISTRY, id(path));
    }

    public TagKey<Block> getBlockTag(String path) {
        var key = id(path);
        var registry = Registry.BLOCK;
        return getTag(registry, key);
    }

    public TagKey<Item> getItemTag(String path) {
        var key = id(path);
        var registry = Registry.ITEM;
        return getTag(registry, key);
    }

    public TagKey<Biome> getBiomeTag(String path) {
        var key = id(path);
        var registry = BuiltinRegistries.BIOME;
        return getTag(registry, key);
    }

    public <T> TagKey<T> getTag(Registry<T> registry, ResourceLocation key) {
        return registry.getTagNames()
                .filter(tag -> tag.location().equals(key))
                .findAny()
                .orElse(TagKey.create(registry.key(), key));
    }

    public boolean modLoaded() {
        return FabricLoader.getInstance().isModLoaded(this.modID);
    }

    public abstract void init();
}
