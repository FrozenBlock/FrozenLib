package net.frozenblock.lib.testmod.config;

import net.frozenblock.lib.FrozenBools;

public final class ClothConfigInteractionHandler {

    public static boolean testBoolean() {
        if (FrozenBools.hasCloth) {
            return TestConfig.get().testBoolean;
        }
        return true;
    }

    public static boolean testSubMenuBoolean() {
        if (FrozenBools.hasCloth) {
            return TestConfig.get().subMenu.testSubMenuBoolean;
        }
        return true;
    }
}
