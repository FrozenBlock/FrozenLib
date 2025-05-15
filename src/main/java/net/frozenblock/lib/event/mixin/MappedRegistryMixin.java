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

package net.frozenblock.lib.event.mixin;

import net.frozenblock.lib.event.api.RegistryFreezeEvents;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin<T> {

	@Inject(method = "freeze", at = @At("HEAD"))
	private void frozenLib$freezeStart(CallbackInfoReturnable<Registry<T>> info) {
		RegistryFreezeEvents.START_REGISTRY_FREEZE.invoker().onStartRegistryFreeze(MappedRegistry.class.cast(this), false);
	}

	@Inject(method = "freeze", at = @At("TAIL"))
	private void frozenLib$freezeEnd(CallbackInfoReturnable<Registry<T>> info) {
		RegistryFreezeEvents.END_REGISTRY_FREEZE.invoker().onEndRegistryFreeze(MappedRegistry.class.cast(this), false);
	}
}
