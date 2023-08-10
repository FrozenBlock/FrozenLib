package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record LocalSoundPacket(double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean distanceDelay) implements FabricPacket {
	public static final PacketType<LocalSoundPacket> PACKET_TYPE = PacketType.create(FrozenMain.LOCAL_SOUND_PACKET, LocalSoundPacket::new);

	public LocalSoundPacket(FriendlyByteBuf buf) {
		this(
			buf.readDouble(),
			buf.readDouble(),
			buf.readDouble(),
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readEnum(SoundSource.class),
			buf.readFloat(),
			buf.readFloat(),
			buf.readBoolean()
		);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeEnum(this.category);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
		buf.writeBoolean(this.distanceDelay);
	}

	@Environment(EnvType.CLIENT)
	public static void receive(LocalSoundPacket packet, LocalPlayer player, PacketSender responseSender) {
		ClientLevel level = player.clientLevel;
		level.playLocalSound(packet.x(), packet.y(), packet.z(), packet.sound(), packet.category(), packet.volume(), packet.pitch(), packet.distanceDelay());
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
