/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.advancement.mixin;

import java.util.List;
import net.frozenblock.lib.advancement.impl.AdvancementRequirementsInterface;
import net.minecraft.advancements.AdvancementRequirements;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AdvancementRequirements.class)
public class AdvancementRequirementsMixin implements AdvancementRequirementsInterface {
	@Mutable
	@Final
	@Shadow
	private List<List<String>> requirements;

	@Override
	public List<List<String>> frozenLib$getRequirements() {
		return this.requirements;
	}

	@Override
	public void frozenLib$setRequirements(List<List<String>> requirements) {
		this.requirements = requirements;
	}
}
