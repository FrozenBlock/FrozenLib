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

package net.frozenblock.lib.event.mixin.neoforge;

import net.frozenblock.lib.event.api.events.BlockEntityLifecycleEvents;
import net.frozenblock.lib.event.api.events.ClientBlockEntityLifecycleEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityLifecycleMixin {

	@Inject(method = "setLevel", at = @At("TAIL"))
	private void frozenLib$onLoad(Level level, CallbackInfo info) {
		final BlockEntity self = (BlockEntity) (Object) this;
		if (level instanceof ServerLevel serverLevel) {
			BlockEntityLifecycleEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(self, serverLevel);
		} else if (level instanceof ClientLevel clientLevel) {
			ClientBlockEntityLifecycleEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(self, clientLevel);
		}
	}

	@Inject(method = "setRemoved", at = @At("TAIL"))
	private void frozenLib$onRemoved(CallbackInfo info) {
		final BlockEntity self = (BlockEntity) (Object) this;
		final Level level = self.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			BlockEntityLifecycleEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(self, serverLevel);
		} else if (level instanceof ClientLevel clientLevel) {
			ClientBlockEntityLifecycleEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(self, clientLevel);
		}
	}
}
