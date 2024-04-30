/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.screenshake.mixin;

import net.frozenblock.lib.screenshake.impl.EntityScreenShakeInterface;
import net.frozenblock.lib.screenshake.impl.EntityScreenShakeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
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

	@Unique @Nullable
	private CompoundTag frozenLib$savedScreenShakesTag;
	@Unique
	private boolean frozenLib$hasSyncedScreenShakes = false;

	@Inject(method = "tick", at = @At(value = "TAIL"))
	public void frozenLib$syncScreenShakes(CallbackInfo info) {
		EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface)ServerPlayer.class.cast(this)).getScreenShakeManager();
		if (!this.frozenLib$hasSyncedScreenShakes && this.connection != null && this.connection.isAcceptingMessages() && !this.isChangingDimension) {
			entityScreenShakeManager.syncWithPlayer(ServerPlayer.class.cast(this));
			this.frozenLib$hasSyncedScreenShakes = true;
		}
	}

	@Inject(method = "changeDimension", at = @At(value = "HEAD"))
	public void frozenLib$changeDimensionSaveScreenShakes(ServerLevel destination, CallbackInfoReturnable<Entity> info) {
		CompoundTag tempTag = new CompoundTag();
		EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface)ServerPlayer.class.cast(this)).getScreenShakeManager();
		entityScreenShakeManager.save(tempTag);
		this.frozenLib$savedScreenShakesTag = tempTag;
	}

	@Inject(method = "changeDimension", at = @At(value = "RETURN"))
	public void frozenLib$changeDimensionLoadScreenShakes(ServerLevel destination, CallbackInfoReturnable<Entity> info) {
		if (this.frozenLib$savedScreenShakesTag != null) {
			EntityScreenShakeManager entityScreenShakeManager = ((EntityScreenShakeInterface)ServerPlayer.class.cast(this)).getScreenShakeManager();
			entityScreenShakeManager.load(this.frozenLib$savedScreenShakesTag);
			this.frozenLib$hasSyncedScreenShakes = false;
		}
	}

}
