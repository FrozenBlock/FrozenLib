package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record LocalPlayerSoundPacket(SoundEvent sound, float volume, float pitch) implements FabricPacket {
	public static final PacketType<LocalPlayerSoundPacket> PACKET_TYPE = PacketType.create(FrozenMain.LOCAL_PLAYER_SOUND_PACKET, LocalPlayerSoundPacket::new);

	public LocalPlayerSoundPacket(FriendlyByteBuf buf) {
		this(
			buf.readById(BuiltInRegistries.SOUND_EVENT),
			buf.readFloat(),
			buf.readFloat()
		);
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeId(BuiltInRegistries.SOUND_EVENT, this.sound);
		buf.writeFloat(this.volume);
		buf.writeFloat(this.pitch);
	}

	@Environment(EnvType.CLIENT)
	public static void receive(LocalPlayerSoundPacket packet, LocalPlayer player, PacketSender responseSender) {
		Minecraft.getInstance().getSoundManager().play(new EntityBoundSoundInstance(packet.sound(), SoundSource.PLAYERS, packet.volume(), packet.pitch(), player, player.clientLevel.random.nextLong()));
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
