package net.frozenblock.lib.block.impl.fire;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;

public record FireData(Holder<FireType> type, boolean permanent) {
	public static final Codec<FireData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		FireType.CODEC.fieldOf("fire_type").forGetter(FireData::type),
		Codec.BOOL.optionalFieldOf("permanent", true).forGetter(FireData::permanent)
	).apply(instance, FireData::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, FireData> STREAM_CODEC = StreamCodec.composite(
		FireType.STREAM_CODEC, FireData::type,
		ByteBufCodecs.BOOL, FireData::permanent,
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

	public static boolean hasPermanentFireData(Entity entity) {
		final FireData fireData = entity.getAttached(ATTACHMENT);
		return fireData != null && fireData.permanent();
	}

	public static boolean canFireDataBeReplaced(Entity entity) {
		final FireData fireData = entity.getAttached(ATTACHMENT);
		if (fireData == null) return true;
		return !hasPermanentFireData(entity) && fireData.type().value().replaceable();
	}

	public static void trySet(Entity entity, ResourceKey<FireType> type) {
		trySet(entity, type, false);
	}

	public static void trySet(Entity entity, Holder<FireType> type) {
		trySet(entity, type, false);
	}

	public static void trySet(Entity entity, ResourceKey<FireType> type, boolean permanent) {
		trySet(entity, entity.registryAccess().getOrThrow(FrozenLibRegistries.FIRE_TYPE).value().getOrThrow(type), permanent);
	}

	public static void trySet(Entity entity, Holder<FireType> type, boolean permanent) {
		if (entity == null || !canFireDataBeReplaced(entity)) return;

		entity.setAttached(ATTACHMENT, new FireData(type, permanent));
	}
}
