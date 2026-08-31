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

import java.util.List;
import net.frozenblock.lib.renderer.model.InvertibleModelPart;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@ClientOnly
@Mixin(ModelPart.class)
public class ModelPartMixin implements InvertibleModelPart {

	@Shadow
	@Final
	private List<ModelPart.Cube> cubes;

	@Unique
	@Override
	public void frozenLib$invert() {
		for (ModelPart.Cube cube : cubes) {
			if (!(cube instanceof InvertibleModelPart invertInterface)) continue;
			invertInterface.frozenLib$invert();
		}
	}
}
