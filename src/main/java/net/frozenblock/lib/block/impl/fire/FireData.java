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

package net.frozenblock.lib.block.impl.fire;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.block.api.fire.FireEvents;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;

public record FireData(Holder<FireType> type) {
	public static final Codec<FireData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		FireType.CODEC.fieldOf("fire_type").forGetter(FireData::type)
	).apply(instance, FireData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, FireData> STREAM_CODEC = StreamCodec.composite(
		FireType.STREAM_CODEC, FireData::type,
		FireData::new
	);
	public static final AttachmentType<FireData> ATTACHMENT = AttachmentRegistry.create(
		FrozenLibConstants.id("fire_data"),
		builder -> {
			builder.persistent(CODEC);
			builder.syncWith(STREAM_CODEC, AttachmentSyncPredicate.all());
		}
	);

	public static void init() {}

	public static boolean canFireDataBeReplaced(Entity entity, Holder<FireType> newType) {
		if (!newType.value().isEnabled()) return false;
		final FireData fireData = entity.getAttached(ATTACHMENT);
		return fireData == null || fireData.type().value().spreadSettings().replaceableByOtherFireTypes();
	}

	public static void trySet(Entity entity, ResourceKey<FireType> type) {
		entity.registryAccess().lookup(FrozenLibRegistries.FIRE_TYPE)
			.flatMap(registry -> registry.get(type))
			.ifPresent(fireType -> trySet(entity, fireType));
	}

	public static void trySet(Entity entity, Holder<FireType> type) {
		if (entity == null || !canFireDataBeReplaced(entity, type) || entity.is(type.value().spreadSettings().cannotApplyToEntityTypes())) return;

		entity.setAttached(ATTACHMENT, new FireData(type));
		FireEvents.AFTER_FIRE_TYPE_SET.invoker().onEntityFireTypeSet(entity, type);
	}
}
