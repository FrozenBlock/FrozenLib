package net.frozenblock.lib.integration.api;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public abstract class ModIntegration {

    private final String modID;

    public ModIntegration(String modID) {
        this.modID = modID;
    }

    public String getID() {
        return this.modID;
    }

    public ResourceLocation id(String path) {
        return new ResourceLocation(this.modID, path);
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

    public boolean modLoaded() {
        return FabricLoader.getInstance().isModLoaded(this.modID);
    }

    public abstract void init();
}
