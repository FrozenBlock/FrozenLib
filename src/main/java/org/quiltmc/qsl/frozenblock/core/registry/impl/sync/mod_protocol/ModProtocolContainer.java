/*
 * Copyright 2023-2024 The Quilt Project
 * Copyright 2023-2024 FrozenBlock
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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Map;
import net.frozenblock.lib.FrozenSharedConstants;
import org.jetbrains.annotations.NotNull;

public interface ModProtocolContainer {
	Codec<Map<String, IntList>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(Codec.INT).xmap(IntArrayList::new, ArrayList::new));

	@NotNull
	static <E> Codec<E> createCodec(Codec<E> codec) {
		final String modProtocol = FrozenSharedConstants.string("mod_protocol");

		return new Codec<>() {
			@Override
			public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
				var value = codec.decode(ops, input);
				if (value.get().right().isPresent()) {
					return value;
				}

				ops.get(input, modProtocol).get().ifLeft((x) -> {
					var versionData = MAP_CODEC.decode(ops, x);
					versionData.get().ifLeft(y ->
						((ModProtocolContainer) value.result().orElseThrow().getFirst()).frozenLib$setModProtocol(y.getFirst())
					);
				});

				return value;
			}

			@Override
			public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
				var value = codec.encode(input, ops, prefix);
				var modProto = ModProtocolContainer.of(input).frozenLib$getModProtocol();

				if (value.get().left().isPresent() && modProto != null) {
					var x = MAP_CODEC.encodeStart(ops, modProto);

					if (x.get().left().isPresent()) {
						return DataResult.success(ops.set(value.result().orElseThrow(), modProtocol, x.result().orElseThrow()));
					}
				}

				return value;
			}
		};
	}

	void frozenLib$setModProtocol(Map<String, IntList> map);
	Map<String, IntList> frozenLib$getModProtocol();

	static ModProtocolContainer of(Object object) {
		return (ModProtocolContainer) object;
	}
}
