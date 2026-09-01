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

package net.frozenblock.lib.renderer.model;

import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.Nullable;

/**
 * Implemented separately on Fabric and NeoForge.
 * <p>
 * Fabric: Redirects to {@code FabricModel} and {@code ModelExtensions}.
 * <p>
 * NeoForge: Copies Fabric's implementation.
 */
@ClientOnly
public interface ModelExtension {

	default void frozenLib$calculateChildParts(ModelPart root) {
		throw new AssertionError();
	}

	@Nullable
	default ModelPart frozenLib$getChildPart(String name) {
		throw new AssertionError();
	}

	default void frozenLib$copyTransforms(Model<?> model) {
		throw new AssertionError();
	}
}
