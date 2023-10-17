package net.frozenblock.lib;

import net.frozenblock.lib.gravity.api.GravityAPI;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class GravityTests {

    @Test
    void testGravity() {
        // gravity 0.1 y 300..319
        GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityAPI.GravityBelt<>(300, 319, true, true, new GravityAPI.AbsoluteGravityFunction(0.1)));
        // gravity 100.0 y -64..-32
        GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityAPI.GravityBelt<>(-64, -32, true, true, new GravityAPI.AbsoluteGravityFunction(100.0)));
        // gravity 0.5 y 0..15
        GravityAPI.register(BuiltinDimensionTypes.OVERWORLD, new GravityAPI.GravityBelt<>(0, 15, true, true, new GravityAPI.AbsoluteGravityFunction(0.5)));

        assertEquals(GravityAPI.calculateGravity(BuiltinDimensionTypes.OVERWORLD, 300), 0.1);
        assertEquals(GravityAPI.calculateGravity(BuiltinDimensionTypes.OVERWORLD, -64), 100.0);
        assertEquals(GravityAPI.calculateGravity(BuiltinDimensionTypes.OVERWORLD, 0), 0.5);
    }
}