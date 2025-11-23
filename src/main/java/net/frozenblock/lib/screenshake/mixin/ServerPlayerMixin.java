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

package net.frozenblock.lib.screenshake.mixin;

import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@Shadow
	public ServerGamePacketListenerImpl connection;
	@Shadow
	private boolean isChangingDimension;

	@Shadow
	@Final
	private static Logger LOGGER;
	@Unique
	@Nullable
	private CompoundTag frozenLib$savedScreenShakesTag;
	@Unique
	private boolean frozenLib$hasSyncedScreenShakes = false;

	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void frozenLib$syncScreenShakes(CallbackInfo info) {
		if (this.frozenLib$hasSyncedScreenShakes || this.connection == null || !this.connection.isAcceptingMessages() || this.isChangingDimension) return;

		final EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface) ServerPlayer.class.cast(this)).frozenLib$getScreenShakeManager();
		entityScreenShakeManager.syncWithPlayer(ServerPlayer.class.cast(this));
		this.frozenLib$hasSyncedScreenShakes = true;
	}

	@Inject(
		method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;",
		at = @At(value = "HEAD")
	)
	public void frozenLib$changeDimensionSaveScreenShakes(TeleportTransition transition, CallbackInfoReturnable<Entity> entityCallbackInfoReturnable) {
		CompoundTag tempTag;

		final ServerPlayer player = ServerPlayer.class.cast(this);
		try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(player.problemPath(), LOGGER)) {
			final TagValueOutput output = TagValueOutput.createWithContext(scopedCollector, player.registryAccess());
			final EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface) ServerPlayer.class.cast(this)).frozenLib$getScreenShakeManager();
			entityScreenShakeManager.save(output);
			tempTag = output.buildResult();
		} catch (Exception e) {
			tempTag = new CompoundTag();
		}

		this.frozenLib$savedScreenShakesTag = tempTag;
	}

	@Inject(
		method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;",
		at = @At(value = "RETURN")
	)
	public void frozenLib$changeDimensionLoadScreenShakes(TeleportTransition transition, CallbackInfoReturnable<Entity> info) {
		if (this.frozenLib$savedScreenShakesTag == null) return;

		final ServerPlayer player = ServerPlayer.class.cast(this);
		try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(player.problemPath(), LOGGER)) {
			ValueInput input = TagValueInput.create(scopedCollector, player.registryAccess(), this.frozenLib$savedScreenShakesTag);
			EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface) ServerPlayer.class.cast(this)).frozenLib$getScreenShakeManager();
			entityScreenShakeManager.load(input);
			this.frozenLib$hasSyncedScreenShakes = false;
		}
	}
}
