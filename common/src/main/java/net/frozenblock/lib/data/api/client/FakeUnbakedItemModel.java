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

import com.mojang.serialization.MapCodec;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ResolvableModel;
import org.joml.Matrix4fc;

@ClientOnly
public record FakeUnbakedItemModel(ItemModel wrapped) implements ItemModel.Unbaked {

	@Override
	public MapCodec<? extends ItemModel.Unbaked> type() {
		return null;
	}

	@Override
	public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
		return this.wrapped;
	}

	@Override
	public void resolveDependencies(ResolvableModel.Resolver resolver) {}
}
