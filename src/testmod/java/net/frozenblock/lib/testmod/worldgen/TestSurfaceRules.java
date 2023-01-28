package net.frozenblock.lib.testmod.worldgen;

import net.frozenblock.lib.worldgen.surface.api.FrozenDimensionBoundRuleSource;
import net.frozenblock.lib.worldgen.surface.api.FrozenSurfaceRuleEntrypoint;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.ArrayList;

public class TestSurfaceRules implements FrozenSurfaceRuleEntrypoint {
    @Override
    public void addOverworldSurfaceRules(ArrayList<SurfaceRules.RuleSource> context) {
        // When in doubt, T R A N S. Seed 7205143747332514273 is a good one for testing.
        SurfaceRules.ConditionSource blueNoise1 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.05, 0.1);
        SurfaceRules.ConditionSource pinkNoise1 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.1, 0.15);
        SurfaceRules.ConditionSource whiteNoise = SurfaceRules.noiseCondition(Noises.CALCITE, 0.15, 0.20);
        SurfaceRules.ConditionSource pinkNoise2 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.20, 0.25);
        SurfaceRules.ConditionSource blueNoise2 = SurfaceRules.noiseCondition(Noises.CALCITE, 0.25, 0.30);

        SurfaceRules.RuleSource LIGHT_BLUE_CONCRETE = SurfaceRules.state(Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState());
        SurfaceRules.RuleSource PINK_CONCRETE = SurfaceRules.state(Blocks.PINK_CONCRETE.defaultBlockState());
        SurfaceRules.RuleSource WHITE_CONCRETE = SurfaceRules.state(Blocks.WHITE_CONCRETE.defaultBlockState());

        context.add(
                SurfaceRules.ifTrue(
                        SurfaceRules.abovePreliminarySurface(),
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(blueNoise1, LIGHT_BLUE_CONCRETE),
                                        SurfaceRules.ifTrue(pinkNoise1, PINK_CONCRETE),
                                        SurfaceRules.ifTrue(whiteNoise, WHITE_CONCRETE),
                                        SurfaceRules.ifTrue(pinkNoise2, PINK_CONCRETE),
                                        SurfaceRules.ifTrue(blueNoise2, LIGHT_BLUE_CONCRETE)
                                )
                        )
                )
        );
    }

    @Override
    public void addOverworldSurfaceRulesNoPrelimSurface(ArrayList<SurfaceRules.RuleSource> context) {
    }

    @Override
    public void addNetherSurfaceRules(ArrayList<SurfaceRules.RuleSource> context) {
    }

    @Override
    public void addEndSurfaceRules(ArrayList<SurfaceRules.RuleSource> context) {
    }

    @Override
    public void addSurfaceRules(ArrayList<FrozenDimensionBoundRuleSource> context) {
    }
}
