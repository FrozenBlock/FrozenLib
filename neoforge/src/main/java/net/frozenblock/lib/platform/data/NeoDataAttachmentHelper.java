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

import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.data.DataAttachmentType;
import net.frozenblock.lib.platform.service.DataAttachmentHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class NeoDataAttachmentHelper implements DataAttachmentHelper {
	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FrozenLibConstants.MOD_ID);

	public static void register(IEventBus modBus) {
		ATTACHMENT_TYPES.register(modBus);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> DataAttachmentType<T> create(DataAttachmentType.Builder<T> builder) {
		DeferredHolder<AttachmentType<?>, AttachmentType<T>> holder = ATTACHMENT_TYPES.register(
			builder.id().getPath(),
			() -> {
				AttachmentType.Builder<T> attachmentBuilder = AttachmentType.builder(() -> (T) null);
				if (builder.codec() != null) attachmentBuilder.serialize(builder.codec().fieldOf("value"));
				if (builder.streamCodec() != null) {
					final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec = builder.streamCodec();
					attachmentBuilder.sync(streamCodec);
				}
				return attachmentBuilder.build();
			}
		);
		return new NeoDataAttachmentType<>(holder);
	}

	private record NeoDataAttachmentType<T>(DeferredHolder<AttachmentType<?>, AttachmentType<T>> holder) implements DataAttachmentType<T> {

		@Override
		public T get(Object holder) {
			return ((IAttachmentHolder) holder).getExistingDataOrNull(this.holder.get());
		}

		@Override
		public T getOrDefault(Object holder, T fallback) {
			return ((IAttachmentHolder) holder).getExistingData(this.holder.get()).orElse(fallback);
		}

		@Override
		public void set(Object holder, T value) {
			((IAttachmentHolder) holder).setData(this.holder.get(), value);
		}

		@Override
		public void remove(Object holder) {
			((IAttachmentHolder) holder).removeData(this.holder.get());
		}

		@Override
		public boolean has(Object holder) {
			return ((IAttachmentHolder) holder).hasData(this.holder.get());
		}
	}
}
