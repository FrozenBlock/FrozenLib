package net.frozenblock.lib.silentticker;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class SilentTicker {
	private Vec3 pos;
	private final Level level;
	private int ticks;
	private boolean remove;
	private final int id;
	private final ResourceLocation type;

	private final List<ServerPlayer> trackingPlayers = new ArrayList<>();
	private final List<ServerPlayer> previousTrackingPlayers = new ArrayList<>();

	public SilentTicker(Level level, Vec3 pos) {
		this(level, pos, 0, SilentTickerManager.getNextIdAndIncrement());
	}

	public SilentTicker(Level level, Vec3 pos, int ticks) {
		this(level, pos, ticks, SilentTickerManager.getNextIdAndIncrement());
	}

	public SilentTicker(Level level, Vec3 pos, int ticks, int id) {
		this.level = level;
		this.pos = pos;
		this.ticks = ticks;
		this.id = id;
		this.type = SilentTickerManager.getTickerResourceLocation(this.getClass());
		SilentTickerManager.addSilentTicker(this);
	}

	public void baseTick() {
		BlockPos blockPos = this.getBlockPos();
		if (this.level instanceof ServerLevel serverLevel) {
			this.previousTrackingPlayers.clear();
			this.previousTrackingPlayers.addAll(this.trackingPlayers);
			this.trackingPlayers.clear();
			this.trackingPlayers.addAll(serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(blockPos), false));
			FriendlyByteBuf byteBuf = null;
			for (ServerPlayer serverPlayer : this.trackingPlayers) {
				if (!previousTrackingPlayers.contains(serverPlayer)) {
					if (byteBuf == null) {
						byteBuf = this.constructPacket();
					}
					ServerPlayNetworking.send(serverPlayer, FrozenMain.SILENT_TICKER_PACKET, byteBuf);
				}
			}
		}
		this.ticks += 1;
		this.tick(this.level, this.pos, blockPos, this.ticks);
	}

	private FriendlyByteBuf constructPacket() {
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeResourceLocation(this.type);
		byteBuf.writeDouble(this.pos.x);
		byteBuf.writeDouble(this.pos.y);
		byteBuf.writeDouble(this.pos.z);
		byteBuf.writeInt(this.ticks);
		byteBuf.writeInt(this.id);
		this.writeExtraPacketData(byteBuf);
		return byteBuf;
	}

	public abstract void writeExtraPacketData(FriendlyByteBuf byteBuf);

	public static void readPacket(Level level, FriendlyByteBuf byteBuf) {
		//TODO: why doesnt this work
		/*
		ResourceLocation location = byteBuf.readResourceLocation();
		Vec3 pos = new Vec3(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble());
		int ticks = byteBuf.readInt();
		int id = byteBuf.readInt();
		SilentTickerManager.SilentTickerFactory<? extends SilentTicker> factory = SilentTickerManager.getTickerFactory(location);
		if (factory != null) {
			factory.create(level, pos, ticks, id, byteBuf);
		}
		 */
	}

	public abstract void tick(Level level, Vec3 vec3, BlockPos pos, int ticks);

	public Vec3 getPos() {
		return this.pos;
	}

	public BlockPos getBlockPos() {
		return new BlockPos(this.getPos());
	}

	public ChunkPos getChunkPos() {
		return new ChunkPos(this.getBlockPos());
	}

	public Level getLevel() {
		return this.level;
	}

	public int getTicks() {
		return this.ticks;
	}

	public int getId() {
		return this.getId();
	}

	public boolean isRemoved() {
		return this.remove;
	}

	public void remove() {
		this.remove = true;
	}

}
