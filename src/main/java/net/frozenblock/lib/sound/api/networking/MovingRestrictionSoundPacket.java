package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.frozenblock.lib.sound.api.instances.RestrictedMovingSound;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public record MovingRestrictionSoundPacket(int id, SoundEvent sound, SoundSource category, float volume, float pitch, ResourceLocation predicateId, boolean stopOnDeath) implements FabricPacket {
	public static final PacketType<MovingRestrictionSoundPacket> PACKET_TYPE = PacketType.create(FrozenMain.MOVING_RESTRICTION_SOUND_PACKET, MovingRestrictionSoundPacket::new);

	public MovingRestrictionSoundPacket(FriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readResourceLocation(),
			buf.readBoolean()
		);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
		buf.writeResourceLocation(predicateId);
		buf.writeBoolean(this.stopOnDeath);
	}

	@Environment(EnvType.CLIENT)
	public static <T extends Entity> void receive(MovingRestrictionSoundPacket packet, LocalPlayer player, PacketSender responseSender) {
		ClientLevel level = player.clientLevel;
		T entity = (T) level.getEntity(packet.id());
		if (entity != null) {
			SoundPredicate.LoopPredicate<T> predicate = SoundPredicate.getPredicate(packet.predicateId());
			Minecraft.getInstance().getSoundManager().play(new RestrictedMovingSound<>(entity, packet.sound(), packet.category(), packet.volume(), packet.pitch(), predicate, packet.stopOnDeath()));
		}
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
