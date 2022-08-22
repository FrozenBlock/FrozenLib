package net.frozenblock.lib;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.frozenblock.lib.item.Camera;
import net.frozenblock.lib.item.LootTableWhacker;
import net.minecraft.core.Registry;

import static net.frozenblock.lib.FrozenMain.id;

public class RegisterDev {

    public static final Camera CAMERA = new Camera(new FabricItemSettings());
    public static final LootTableWhacker LOOT_TABLE_WHACKER = new LootTableWhacker(new FabricItemSettings());

    public static void init() {
            Registry.register(Registry.ITEM, id("camera"), CAMERA);
            Registry.register(Registry.ITEM, id("loot_table_whacker"), LOOT_TABLE_WHACKER);
    }

}
