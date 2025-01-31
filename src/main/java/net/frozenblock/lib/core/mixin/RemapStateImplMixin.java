/*
 * Copyright (C) 2025 FrozenBlock
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

package net.frozenblock.lib.core.mixin;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.impl.registry.sync.RemapStateImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemapStateImpl.class)
public class RemapStateImplMixin {

	@Shadow
	@Final
	private Int2ObjectMap<ResourceLocation> oldIdMap;

	@Shadow
	@Final
	private Int2ObjectMap<ResourceLocation> newIdMap;

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	public void onInit(Registry registry, Int2ObjectMap oldIdMap, Int2IntMap rawIdChangeMap, CallbackInfo ci) {
		this.oldIdMap.forEach((oldInt, oldId) -> {
			this.newIdMap.forEach((newInt, newId) -> {
				if (newId.equals(oldId)) {
					System.out.println("(RemapStateImpl) " + registry.key().location().getPath() + ": " + newId + " - " + "remapped: " + newInt + " old: " + oldInt);
				}
			});
		});
	}
}
