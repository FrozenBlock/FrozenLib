/*
 * Copyright (C) 2024-2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.levelgen.structure.api.processor;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;

public final class FrozenLibRuleBlockEntityModifiers {
	private static final DeferredRegister<RuleBlockEntityModifierType<?>> REGISTER = DeferredRegister.create(
		Registries.RULE_BLOCK_ENTITY_MODIFIER,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<RuleBlockEntityModifierType<?>, RuleBlockEntityModifierType<AppendSherds>> APPEND_SHERDS = register(
		"append_sherds", AppendSherds.CODEC
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <P extends RuleBlockEntityModifier> DeferredHolder<RuleBlockEntityModifierType<?>, RuleBlockEntityModifierType<P>> register(
		String name, MapCodec<P> codec
	) {
		return REGISTER.register(name, () -> () -> codec);
	}
}
