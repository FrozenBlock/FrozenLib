package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.sound.api.FlyBySoundHub;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public record FlyBySoundPacket(int id, SoundEvent sound, SoundSource category, float volume, float pitch) implements FabricPacket {
	public static final PacketType<FlyBySoundPacket> PACKET_TYPE = PacketType.create(FrozenMain.FLYBY_SOUND_PACKET, FlyBySoundPacket::new);

	public FlyBySoundPacket(FriendlyByteBuf buf) {
		this(
			buf.readVarInt(),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat()
		);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public static void receive(FlyBySoundPacket packet, LocalPlayer player, PacketSender responseSender) {
		ClientLevel level = player.clientLevel;
		Entity entity = level.getEntity(packet.id());
		if (entity != null) {
			FlyBySoundHub.FlyBySound flyBySound = new FlyBySoundHub.FlyBySound(packet.pitch(), packet.volume(), packet.category(), packet.sound());
			FlyBySoundHub.addEntity(entity, flyBySound);
		}
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
