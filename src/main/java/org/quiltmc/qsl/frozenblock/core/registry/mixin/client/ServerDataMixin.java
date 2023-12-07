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

package org.quiltmc.qsl.frozenblock.core.registry.mixin.client;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ServerData;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.mod_protocol.ModProtocolContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(ServerData.class)
public class ServerDataMixin implements ModProtocolContainer {
	@Unique
	private Map<String, IntList> frozenLib$modProtocol;

	@Override
	public void frozenLib$setModProtocol(Map<String, IntList> map) {
		this.frozenLib$modProtocol = map;
	}

	@Override
	public Map<String, IntList> frozenLib$getModProtocol() {
		return this.frozenLib$modProtocol;
	}
}
