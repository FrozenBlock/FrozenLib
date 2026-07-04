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

package net.frozenblock.lib.platform.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Supplier;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.data.DataAttachmentTarget;
import net.frozenblock.lib.platform.api.data.DataAttachmentType;
import net.frozenblock.lib.platform.service.DataAttachmentHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class NeoDataAttachmentHelper implements DataAttachmentHelper {
	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FrozenLibConstants.MOD_ID);

	public static void register(IEventBus modBus) {
		ATTACHMENT_TYPES.register(modBus);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> DataAttachmentType<T> create(DataAttachmentType.Builder<T> builder) {
		final Supplier<T> initializer = builder.initializer();
		final var syncPredicate = builder.syncPredicate();
		DeferredHolder<AttachmentType<?>, AttachmentType<T>> holder = ATTACHMENT_TYPES.register(
			builder.id().getPath(),
			() -> {
				Supplier<T> defaultSupplier = initializer != null ? initializer : () -> (T) null;
				AttachmentType.Builder<T> attachmentBuilder = AttachmentType.builder(defaultSupplier);
				if (builder.codec() != null) {
					attachmentBuilder.serialize(builder.codec().fieldOf("value"));
					if (builder.isCopyOnDeath()) attachmentBuilder.copyOnDeath();
				}
				if (builder.streamCodec() != null && syncPredicate != null) {
					final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec = builder.streamCodec();
					attachmentBuilder.sync(syncPredicate::test, streamCodec);
				}
				return attachmentBuilder.build();
			}
		);
		return new NeoDataAttachmentType<>(
			holder,
			builder.id(),
			builder.codec(),
			builder.streamCodec(),
			initializer,
			builder.isCopyOnDeath()
		);
	}

	private static final class NeoDataAttachmentType<T> implements DataAttachmentType<T> {
		private final DeferredHolder<AttachmentType<?>, AttachmentType<T>> holder;
		private final Identifier id;
		@Nullable
		private final Codec<T> codec;
		@Nullable
		private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
		@Nullable
		private final Supplier<T> initializer;
		private final boolean copyOnDeath;

		private NeoDataAttachmentType(
			DeferredHolder<AttachmentType<?>, AttachmentType<T>> holder,
			Identifier id,
			@Nullable Codec<T> codec,
			@Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec,
			@Nullable Supplier<T> initializer,
			boolean copyOnDeath
		) {
			this.holder = holder;
			this.id = id;
			this.codec = codec;
			this.streamCodec = streamCodec;
			this.initializer = initializer;
			this.copyOnDeath = copyOnDeath;
		}

		@Override
		public Identifier identifier() {
			return this.id;
		}

		@Override
		public @Nullable T get(DataAttachmentTarget holder) {
			return ((IAttachmentHolder) holder).getExistingDataOrNull(this.holder.get());
		}

		@Override
		public T getOrDefault(DataAttachmentTarget holder, T fallback) {
			return ((IAttachmentHolder) holder).getExistingData(this.holder.get()).orElse(fallback);
		}

		@Override
		public void set(DataAttachmentTarget holder, T value) {
			((IAttachmentHolder) holder).setData(this.holder.get(), value);
		}

		@Override
		public void remove(DataAttachmentTarget holder) {
			((IAttachmentHolder) holder).removeData(this.holder.get());
		}

		@Override
		public boolean has(DataAttachmentTarget holder) {
			return ((IAttachmentHolder) holder).hasData(this.holder.get());
		}

		@Override
		public void sync(DataAttachmentTarget holder) {
			((IAttachmentHolder) holder).syncData(this.holder.get());
		}

		@Override
		public @Nullable Supplier<T> initializer() {
			return this.initializer;
		}

		@Override
		public boolean isPersistent() {
			return this.codec != null;
		}

		@Override
		public boolean isSynced() {
			return this.streamCodec != null;
		}

		@Override
		public boolean copyOnDeath() {
			return this.copyOnDeath;
		}
	}
}
