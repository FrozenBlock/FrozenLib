/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.entity.mixin.client.rendering;

import java.util.List;
import net.frozenblock.lib.entity.impl.client.rendering.CubeInvertInterface;
import net.frozenblock.lib.entity.impl.client.rendering.ModelPartInvertInterface;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.class)
public class ModelPartMixin implements ModelPartInvertInterface {

	@Shadow
	@Final
	private List<ModelPart.Cube> cubes;

	@Override
	public void frozenLib$setInverted(boolean inverted) {
		this.cubes.forEach(cube -> {
			if (cube instanceof CubeInvertInterface cubeInvert) {
				cubeInvert.frozenLib$setInverted(inverted);
			}
		});
	}
}
