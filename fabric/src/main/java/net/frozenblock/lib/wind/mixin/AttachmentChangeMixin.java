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

package net.frozenblock.lib.wind.mixin;

import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentTarget;
import net.frozenblock.lib.wind.WindManager;
import net.frozenblock.lib.wind.disturbance.WindDisturbances;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttachmentChange.class)
public class AttachmentChangeMixin {

	/**
	 * @reason Is called to set the attachment on the client when it is synced. We are using this to add {@link WindDisturbances} to the client's {@link WindManager}.
	 */
	@Inject(method = "tryApply", at = @At("TAIL"))
	private void frozenLib$onAttachmentApplied(Level level, CallbackInfo info) {
		if (!level.isClientSide()) return;

		AttachmentChange attachmentChange = AttachmentChange.class.cast(this);
		if (!attachmentChange.type().identifier().equals(WindDisturbances.ATTACHMENT_TYPE.identifier())) return;

		final WindManager windManager = WindManager.getOrCreate(level);
		windManager.trackDisturbanceHolder((DataAttachmentTarget) attachmentChange.targetInfo().getTarget(level));
	}
}
