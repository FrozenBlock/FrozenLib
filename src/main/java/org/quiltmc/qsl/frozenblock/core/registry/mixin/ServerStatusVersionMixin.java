/*
 * Copyright 2023 The Quilt Project
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
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 The Quilt Project
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.protocol.status.ServerStatus;
import org.quiltmc.qsl.frozenblock.core.registry.api.sync.ModProtocol;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.mod_protocol.ModProtocolContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerStatus.Version.class)
public class ServerStatusVersionMixin implements ModProtocolContainer {
	@Mutable
	@Shadow
	@Final
	public static Codec<ServerStatus.Version> CODEC;

	@Unique
	private Map<String, IntList> frozenLib$modProtocol;

	@ModifyReturnValue(method = "current", at = @At("RETURN"))
	private static ServerStatus.Version quilt$addProtocolVersions(ServerStatus.Version original) {
		if (ModProtocol.disableQuery) {
			return null;
		}

		var map = new HashMap<String, IntList>();
		for (var protocol : ModProtocol.REQUIRED) {
			map.put(protocol.id(), protocol.versions());
		}

		((ModProtocolContainer) (Object) original).frozenLib$setModProtocol(map);
		return original;
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void quilt$extendCodec(CallbackInfo ci) {
		CODEC = ModProtocolContainer.createCodec(CODEC);
	}

	@Override
	public void frozenLib$setModProtocol(Map<String, IntList> map) {
		this.frozenLib$modProtocol = map;
	}

	@Override
	public Map<String, IntList> frozenLib$getModProtocol() {
		return this.frozenLib$modProtocol;
	}
}
