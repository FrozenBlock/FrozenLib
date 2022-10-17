package net.frozenblock.lib.replacements_and_lists;

import java.util.ArrayList;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class HopperUntouchableList {

    public static ArrayList<BlockEntityType<?>> blackListedTypes = new ArrayList<>();

    public static boolean inventoryContainsBlacklisted(Container inventory) {
        if (inventory instanceof BlockEntity block) {
            if (blackListedTypes.contains(block.getType())) {
                return true;
            }
        } else if (inventory instanceof CompoundContainer doubleInventory) {
            if (doubleInventory.container1 instanceof BlockEntity block) {
                if (blackListedTypes.contains(block.getType())) {
                    return true;
                }
            }
            if (doubleInventory.container2 instanceof BlockEntity block) {
                if (blackListedTypes.contains(block.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

}
