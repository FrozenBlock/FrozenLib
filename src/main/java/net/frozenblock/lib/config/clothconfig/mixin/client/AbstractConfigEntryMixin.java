/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.config.clothconfig.mixin.client;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.clothconfig.impl.AbstractConfigEntryInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("rawtypes")
@Environment(EnvType.CLIENT)
@Mixin(AbstractConfigEntry.class)
public class AbstractConfigEntryMixin implements AbstractConfigEntryInterface {

	@Unique
	private boolean frozenLib$canSave;

	@Inject(method = "save", at = @At("HEAD"), cancellable = true, remap = false)
	public void frozenLib$save(CallbackInfo info) {
		if (!this.frozenLib$canSave()) {
			info.cancel();
		}
	}

	@Unique
	@Override
	public void frozenLib$setCanSave(boolean canSave) {
		this.frozenLib$canSave = canSave;
	}

	@Unique
	@Override
	public boolean frozenLib$canSave() {
		return this.frozenLib$canSave;
	}
}
