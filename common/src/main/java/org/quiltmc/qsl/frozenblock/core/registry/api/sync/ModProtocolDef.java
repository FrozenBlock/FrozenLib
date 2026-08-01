/*
 * Copyright 2024-2026 The Quilt Project
 * Copyright 2024-2026 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.core.registry.api.sync;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.quiltmc.qsl.frozenblock.core.registry.impl.sync.ProtocolVersions;

public record ModProtocolDef(String id, String displayName, IntList versions, boolean optional) {
	public static final StreamCodec<FriendlyByteBuf, ModProtocolDef> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8,
		ModProtocolDef::id,
		ByteBufCodecs.STRING_UTF8,
		ModProtocolDef::displayName,
		ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.collection(IntArrayList::new)),
		ModProtocolDef::versions,
		ByteBufCodecs.BOOL,
		ModProtocolDef::optional,
		ModProtocolDef::new
	);

	public int latestMatchingVersion(IntCollection versions) {
		return ProtocolVersions.getHighestSupported(versions, this.versions);
	}
}
