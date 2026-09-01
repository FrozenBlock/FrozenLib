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

package net.frozenblock.lib.renderer.mixin.model.invert;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.lib.renderer.model.InvertibleModelPart;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(PartDefinition.class)
public class PartDefinitionMixin implements InvertibleModelPart {

	@Unique
	private boolean frozenLib$inverted;

	@ModifyReturnValue(method = "bake", at = @At("RETURN"))
	public ModelPart frozenLib$invertModelParts(ModelPart modelPart) {
		if (!this.frozenLib$inverted) return modelPart;
		if ((Object)modelPart instanceof InvertibleModelPart invertInterface) invertInterface.frozenLib$invert();
		return modelPart;
	}

	@Unique
	@Override
	public void frozenLib$invert() {
		this.frozenLib$inverted = true;
	}
}
