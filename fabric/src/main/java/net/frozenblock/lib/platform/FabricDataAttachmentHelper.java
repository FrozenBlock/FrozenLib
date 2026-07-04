/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.platform;

import java.util.function.Supplier;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;
import net.frozenblock.lib.networking.PlayerLookup;
import net.frozenblock.lib.platform.api.data.DataAttachmentTarget;
import net.frozenblock.lib.platform.api.data.DataAttachmentType;
import net.frozenblock.lib.platform.service.DataAttachmentHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class FabricDataAttachmentHelper implements DataAttachmentHelper {

	@Override
	public <T> DataAttachmentType<T> create(DataAttachmentType.Builder<T> builder) {
		final var syncPredicate = builder.syncPredicate();
		AttachmentType<T> attachmentType = AttachmentRegistry.create(builder.id(), attachmentBuilder -> {
			if (builder.codec() != null) attachmentBuilder.persistent(builder.codec());
			if (builder.initializer() != null) attachmentBuilder.initializer(builder.initializer());
			if (builder.isCopyOnDeath()) attachmentBuilder.copyOnDeath();
			if (builder.streamCodec() != null && syncPredicate != null)
				attachmentBuilder.syncWith(builder.streamCodec(), syncPredicate::test);
		});
		return new FabricDataAttachmentType<>(attachmentType);
	}

	private record FabricDataAttachmentType<T>(AttachmentType<T> attachmentType) implements DataAttachmentType<T> {

		@Override
		public Identifier identifier() {
			return this.attachmentType.identifier();
		}

		@Override
		public @Nullable T get(DataAttachmentTarget holder) {
			return ((AttachmentTarget) holder).getAttached(this.attachmentType);
		}

		@Override
		public T getOrDefault(DataAttachmentTarget holder, T fallback) {
			return ((AttachmentTarget) holder).getAttachedOrElse(this.attachmentType, fallback);
		}

		@Override
		public void set(DataAttachmentTarget holder, T value) {
			((AttachmentTarget) holder).setAttached(this.attachmentType, value);
		}

		@Override
		public void remove(DataAttachmentTarget holder) {
			((AttachmentTarget) holder).removeAttached(this.attachmentType);
		}

		@Override
		public boolean has(DataAttachmentTarget holder) {
			return ((AttachmentTarget) holder).hasAttached(this.attachmentType);
		}

		@Override
		public void sync(DataAttachmentTarget holder) {
			if (!(holder instanceof ServerLevel serverLevel)) {
				throw new UnsupportedOperationException("Manual sync is only supported for ServerLevel holders, got " + holder);
			}

			final T value = ((AttachmentTarget) holder).getAttached(this.attachmentType);
			final AttachmentChange change = new AttachmentChange(AttachmentTargetInfo.LevelTarget.INSTANCE, this.attachmentType, value);
			for (ServerPlayer player : PlayerLookup.level(serverLevel)) AttachmentSync.trySync(change, player);
		}

		@Override
		public @Nullable Supplier<T> initializer() {
			return this.attachmentType.initializer();
		}

		@Override
		public boolean isPersistent() {
			return this.attachmentType.isPersistent();
		}

		@Override
		public boolean isSynced() {
			return this.attachmentType.isSynced();
		}

		@Override
		public boolean copyOnDeath() {
			return this.attachmentType.copyOnDeath();
		}
	}
}
