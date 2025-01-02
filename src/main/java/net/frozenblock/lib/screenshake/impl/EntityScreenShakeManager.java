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

package net.frozenblock.lib.screenshake.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.frozenblock.lib.FrozenSharedConstants;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class EntityScreenShakeManager {
    private final ArrayList<EntityScreenShake> shakes = new ArrayList<>();
    public Entity entity;

    public EntityScreenShakeManager(Entity entity) {
        this.entity = entity;
    }

    public void load(CompoundTag nbt) {
        if (nbt.contains("ScreenShakes", 9)) {
            this.shakes.clear();
            DataResult<List<EntityScreenShake>> var10000 = EntityScreenShake.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getList("ScreenShakes", 10)));
            Logger var10001 = FrozenSharedConstants.LOGGER4;
            Objects.requireNonNull(var10001);
            Optional<List<EntityScreenShake>> list = var10000.resultOrPartial(var10001::error);
			list.ifPresent(this.shakes::addAll);
        }
    }

    public void save(CompoundTag nbt) {
		if (!this.shakes.isEmpty()) {
			DataResult<Tag> var10000 = EntityScreenShake.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.shakes);
			Logger var10001 = FrozenSharedConstants.LOGGER4;
			Objects.requireNonNull(var10001);
			var10000.resultOrPartial(var10001::error).ifPresent((cursorsNbt) -> nbt.put("ScreenShakes", cursorsNbt));
		}
    }

    public void addShake(float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
        this.shakes.add(new EntityScreenShake(intensity, duration, durationFalloffStart, maxDistance, ticks));
    }

    public void tick() {
		this.shakes.removeIf(EntityScreenShake::shouldRemove);
		for (EntityScreenShake entityScreenShake : this.shakes) {
			entityScreenShake.ticks += 1;
		}
    }

	public void syncWithPlayer(ServerPlayer serverPlayer) {
		for (EntityScreenShake nbt : this.getShakes()) {
			ScreenShakeManager.sendEntityScreenShakeTo(serverPlayer, this.entity, nbt.intensity, nbt.duration, nbt.durationFalloffStart, nbt.maxDistance, nbt.ticks);
		}
	}

	public ArrayList<EntityScreenShake> getShakes() {
		return this.shakes;
	}

	public static class EntityScreenShake {
		public final float intensity;
		public final int duration;
		public final int durationFalloffStart;
		public final float maxDistance;
		public int ticks;

		public static final Codec<EntityScreenShake> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				Codec.FLOAT.fieldOf("Intensity").forGetter(EntityScreenShake::getIntensity),
				Codec.INT.fieldOf("Duration").forGetter(EntityScreenShake::getDuration),
				Codec.INT.fieldOf("FalloffStart").forGetter(EntityScreenShake::getDurationFalloffStart),
				Codec.FLOAT.fieldOf("MaxDistance").forGetter(EntityScreenShake::getMaxDistance),
				Codec.INT.fieldOf("Ticks").forGetter(EntityScreenShake::getTicks)
		).apply(instance, EntityScreenShake::new));

		public EntityScreenShake(float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.maxDistance = maxDistance;
			this.ticks = ticks;
		}

		public boolean shouldRemove() {
			return this.ticks > this.duration;
		}

		public float getIntensity() {
			return this.intensity;
		}

		public int getDuration() {
			return this.duration;
		}

		public int getDurationFalloffStart() {
			return this.durationFalloffStart;
		}

		public float getMaxDistance() {
			return this.maxDistance;
		}

		public int getTicks() {
			return this.ticks;
		}

	}
}
