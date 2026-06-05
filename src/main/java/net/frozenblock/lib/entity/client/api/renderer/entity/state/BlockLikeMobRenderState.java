package net.frozenblock.lib.entity.client.api.renderer.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class BlockLikeMobRenderState extends LivingEntityRenderState {
	public final BlockModelRenderState blockModel = new BlockModelRenderState();
	public BlockState blockState = Blocks.AIR.defaultBlockState();
	public final Quaternionf rotation = new Quaternionf();
	public Vec3 rotationPivot = Vec3.ZERO;
}
