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

package net.frozenblock.lib.music.api.client.pitch;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.music.impl.client.MusicPitchDetectionType;
import net.minecraft.resources.ResourceLocation;

/**
 * @param type The in-game location type to check, in {@link MusicPitchDetectionType} form.
 * @param location The {@link ResourceLocation} of the in-game location that triggers the pitch change.
 * @param pitchFunction The target pitch to play music at. This is passed as a {@link Function} with a {@link Long} as the parameter, allowing pitch to continuously shift.
 */
@Environment(EnvType.CLIENT)
public record MusicPitchInfo(MusicPitchDetectionType type, ResourceLocation location, Function<Long, Float> pitchFunction) {
}
