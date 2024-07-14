package net.frozenblock.lib.worldgen.structure.impl;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.worldgen.structure.api.AppendSherds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;

public class FrozenRuleBlockEntityModifiers {
	public static final RuleBlockEntityModifierType<AppendSherds> APPEND_SHERDS = register("append_sherds", AppendSherds.CODEC);

	public static void init() {
	}

	private static <P extends RuleBlockEntityModifier> RuleBlockEntityModifierType<P> register(String name, MapCodec<P> codec) {
		return Registry.register(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER, FrozenSharedConstants.id(name), () -> codec);
	}
}
