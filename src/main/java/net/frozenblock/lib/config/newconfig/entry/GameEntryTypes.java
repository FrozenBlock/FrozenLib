package net.frozenblock.lib.config.newconfig.entry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public class GameEntryTypes {
	public static final EntryType<Identifier> IDENTIFIER = EntryType.create(Identifier.CODEC, Identifier.STREAM_CODEC);
	public static final EntryType<BlockPos> BLOCKPOS = EntryType.create(BlockPos.CODEC, BlockPos.STREAM_CODEC);
	public static final EntryType<ChunkPos> CHUNKPOS = EntryType.create(ChunkPos.CODEC, ChunkPos.STREAM_CODEC);
	public static final EntryType<Vec3> VEC3 = EntryType.create(Vec3.CODEC, Vec3.STREAM_CODEC);
}
