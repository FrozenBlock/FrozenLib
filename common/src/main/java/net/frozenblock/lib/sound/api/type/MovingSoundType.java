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

package net.frozenblock.lib.sound.api.type;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.api.data.DataAttachmentType;import net.frozenblock.lib.registry.FrozenLibRegistries;import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public abstract class MovingSoundType<D> {
	private final DataAttachmentType<List<D>> attachmentType;

	protected MovingSoundType(Identifier attachmentId, Codec<D> dataCodec) {
		this.attachmentType = DataAttachmentType.create(
			attachmentId,
			builder -> builder.persistent(dataCodec.listOf())
		);
	}

	public DataAttachmentType<List<D>> getAttachmentType() {
		return this.attachmentType;
	}

	public void addSound(Entity entity, D data) {
		final List<D> current = entity.frozenLib$getAttached(this.attachmentType);
		final List<D> next = current == null ? new ArrayList<>() : new ArrayList<>(current);
		next.add(data);
		entity.frozenLib$setAttached(this.attachmentType, next);
		this.onAdd(entity, data);
	}

	protected void onAdd(Entity entity, D data) {}

	protected abstract List<D> tick(Entity entity, List<D> sounds);

	protected abstract void syncWithPlayer(Entity entity, ServerPlayer player, List<D> sounds);

	public final void tickSounds(Entity entity) {
		final List<D> sounds = entity.frozenLib$getAttached(this.attachmentType);
		if (sounds == null || sounds.isEmpty()) return;
		entity.frozenLib$setAttached(this.attachmentType, this.tick(entity, sounds));
	}

	public final void syncSounds(Entity entity, ServerPlayer player) {
		final List<D> sounds = entity.frozenLib$getAttached(this.attachmentType);
		if (sounds == null || sounds.isEmpty()) return;
		this.syncWithPlayer(entity, player, sounds);
	}

	public static <D> MovingSoundType<D> register(Identifier id, MovingSoundType<D> type) {
		return Registry.register(FrozenLibRegistries.MOVING_SOUND_TYPE, id, type);
	}
}
