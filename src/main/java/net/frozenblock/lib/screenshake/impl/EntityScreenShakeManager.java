/*
 * Copyright (C) 2024-2026 FrozenBlock
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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import net.frozenblock.lib.screenshake.api.ScreenShakeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EntityScreenShakeManager {
	private final ArrayList<EntityScreenShake> shakes = new ArrayList<>();
	public Entity entity;

	public EntityScreenShakeManager(Entity entity) {
		this.entity = entity;
	}

	public void load(ValueInput input) {
		this.shakes.clear();
		final ValueInput.TypedInputList<EntityScreenShake> list = input.listOrEmpty("frozenlib_screen_shakes", EntityScreenShake.CODEC);
		for (EntityScreenShake shake : list) this.shakes.add(shake);
	}

	public void save(ValueOutput nbt) {
		if (this.shakes.isEmpty()) return;

		final ValueOutput.TypedOutputList<EntityScreenShake> list = nbt.list("frozenlib_screen_shakes", EntityScreenShake.CODEC);
		for (EntityScreenShake shake : this.shakes) list.add(shake);
	}

	public void addShake(float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
		this.shakes.add(new EntityScreenShake(intensity, duration, durationFalloffStart, maxDistance, ticks));
	}

	public void tick() {
		this.shakes.removeIf(EntityScreenShake::hasDurationExpired);
		for (EntityScreenShake entityScreenShake : this.shakes) entityScreenShake.tick();
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
		public static final Codec<EntityScreenShake> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.FLOAT.fieldOf("Intensity").forGetter(EntityScreenShake::intensity),
			Codec.INT.fieldOf("Duration").forGetter(EntityScreenShake::duration),
			Codec.INT.fieldOf("FalloffStart").forGetter(EntityScreenShake::durationFalloffStart),
			Codec.FLOAT.fieldOf("MaxDistance").forGetter(EntityScreenShake::maxDistance),
			Codec.INT.fieldOf("Ticks").forGetter(EntityScreenShake::ticks)
		).apply(instance, EntityScreenShake::new));

		final float intensity;
		final int duration;
		final int durationFalloffStart;
		final float maxDistance;
		public int ticks;

		public EntityScreenShake(float intensity, int duration, int durationFalloffStart, float maxDistance, int ticks) {
			this.intensity = intensity;
			this.duration = duration;
			this.durationFalloffStart = durationFalloffStart;
			this.maxDistance = maxDistance;
			this.ticks = ticks;
		}

		public void tick() {
			this.ticks += 1;
		}

		public float intensity() {
			return this.intensity;
		}

		public int duration() {
			return this.duration;
		}

		public int durationFalloffStart() {
			return this.durationFalloffStart;
		}

		public float maxDistance() {
			return this.maxDistance;
		}

		public int ticks() {
			return this.ticks;
		}

		public boolean hasDurationExpired() {
			return this.ticks > this.duration;
		}
	}
}
