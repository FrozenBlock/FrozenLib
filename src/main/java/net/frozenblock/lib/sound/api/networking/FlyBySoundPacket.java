package net.frozenblock.lib.sound.api.networking;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

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

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
