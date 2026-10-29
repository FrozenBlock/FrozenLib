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

package net.frozenblock.lib.data.api.client;

import com.mojang.math.Transformation;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;
import java.util.Objects;

@ClientOnly
public final class NamedModelState implements ModelState {
	private final String name;
	private final ModelState wrapped;

	NamedModelState(String name, ModelState modelState) {
		this.name = name;
		this.wrapped = modelState;
	}

	public static NamedModelState createOxidation(int stage, ModelState modelState) {
		return new NamedModelState("oxidation_" + stage, modelState);
	}

	public static NamedModelState create(String name, ModelState modelState) {
		return new NamedModelState(name, modelState);
	}

	public static NamedModelState create(Identifier name, ModelState modelState) {
		return new NamedModelState(name.toString(), modelState);
	}

	@Override
	public Transformation transformation() {
		return this.wrapped.transformation();
	}

	@Override
	public Matrix4fc faceTransformation(Direction direction) {
		return this.wrapped.faceTransformation(direction);
	}

	@Override
	public Matrix4fc inverseFaceTransformation(Direction direction) {
		return this.wrapped.inverseFaceTransformation(direction);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj
			|| (obj instanceof NamedModelState other && other.wrapped == this.wrapped && Objects.equals(other.name, this.name));
	}
}
