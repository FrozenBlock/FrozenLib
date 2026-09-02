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

package net.frozenblock.lib.levelgen.material.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.levelgen.material.impl.NoiseGeneratorSettingsInterface;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = NoiseGeneratorSettings.class, priority = 990) // Apply before default mods
public class NoiseGeneratorSettingsMixin implements NoiseGeneratorSettingsInterface {

	/**
	 * Surface rules added by FrozenLib
	 */
	@Unique
	private MaterialRule frozenLib$materialRuleAddition;

	@ModifyReturnValue(method = "materialRule", at = @At("RETURN"))
	private Holder<MaterialRule> frozenLib$modifyRules(Holder<MaterialRule> original) {
		if (this.frozenLib$materialRuleAddition == null) return original;
		return Holder.direct(MaterialRules.sequence(this.frozenLib$materialRuleAddition, original.value()));
	}

	@Unique
	@Override
	public void frozenLib$setMaterialRuleAddition(MaterialRule materialRule) {
		if (materialRule == null || materialRule == this.frozenLib$materialRuleAddition) return;
		this.frozenLib$materialRuleAddition = materialRule;
	}
}
