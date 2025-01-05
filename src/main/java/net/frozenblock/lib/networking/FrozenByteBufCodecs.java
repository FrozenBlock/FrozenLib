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

package net.frozenblock.lib.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FrozenByteBufCodecs {

	public static final StreamCodec<FriendlyByteBuf, Vec3> VEC3 = new StreamCodec<>() {
		@NotNull
        @Override
        public Vec3 decode(@NotNull FriendlyByteBuf buf) {
            return buf.readVec3();
        }

        @Override
        public void encode(@NotNull FriendlyByteBuf buf, Vec3 vec) {
            buf.writeVec3(vec);
        }
    };
}
