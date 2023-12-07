/*
 * Copyright 2022 The Quilt Project
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
 */

package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.QuiltSyncTask;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.server.SyncTaskHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerMixin implements SyncTaskHolder {

	@Shadow
	@Nullable
	private ConfigurationTask currentTask;

	@Shadow
	public abstract void finishCurrentTask(ConfigurationTask.Type taskType);

	@Override
	public @Nullable QuiltSyncTask frozenLib$getQuiltSyncTask() {
		if (this.currentTask instanceof QuiltSyncTask task) return task;
		throw new IllegalStateException("Not currently in QuiltSyncTask!");
	}

	@Override
	public void frozenLib$finishQuiltSyncTask() {
		this.finishCurrentTask(QuiltSyncTask.TYPE);
	}
}
