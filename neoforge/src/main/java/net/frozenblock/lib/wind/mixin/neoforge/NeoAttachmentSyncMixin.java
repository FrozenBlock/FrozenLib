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

package net.frozenblock.lib.wind.mixin.neoforge;

import java.util.List;
import net.frozenblock.lib.platform.api.data.DataAttachmentTarget;
import net.frozenblock.lib.wind.WindManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.attachment.AttachmentSync;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttachmentSync.class)
public class NeoAttachmentSyncMixin {

	@Inject(method = "receiveSyncedDataAttachments", at = @At("TAIL"))
	private static void frozenLib$onReceiveSyncedDataAttachments(
		AttachmentHolder holder,
		RegistryAccess registryAccess,
		List<AttachmentType<?>> types,
		byte[] bytes,
		CallbackInfo info
	) {
		final Level level;
		final DataAttachmentTarget target;
		if (holder instanceof Entity entity) {
			level = entity.level();
			target = entity;
		} else if (holder instanceof BlockEntity blockEntity) {
			level = blockEntity.getLevel();
			target = blockEntity;
		} else {
			return;
		}

		if (level == null || !level.isClientSide()) return;
		WindManager.getOrCreate(level).trackOrUntrackDisturbanceHolder(target);
	}
}
