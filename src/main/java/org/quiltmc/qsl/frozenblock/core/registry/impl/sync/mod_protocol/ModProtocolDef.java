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
 */

package org.quiltmc.qsl.frozenblock.core.registry.impl.sync.mod_protocol;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.network.FriendlyByteBuf;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;

public record ModProtocolDef(String id, String displayName, IntList versions, boolean optional) {
	public static void write(FriendlyByteBuf buf, ModProtocolDef def) {
		buf.writeUtf(def.id);
		buf.writeUtf(def.displayName);
		buf.writeIntIdList(def.versions);
		buf.writeBoolean(def.optional);
	}

	public static ModProtocolDef read(FriendlyByteBuf buf) {
		var id = buf.readUtf();
		var name = buf.readUtf();
		var versions = buf.readIntIdList();
		var optional = buf.readBoolean();
		return new ModProtocolDef(id, name, versions, optional);
	}

	public int latestMatchingVersion(IntCollection versions) {
		return ProtocolVersions.getHighestSupported(versions, this.versions);
	}
}
