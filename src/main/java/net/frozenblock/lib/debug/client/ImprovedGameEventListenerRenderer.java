package net.frozenblock.lib.debug.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenClient;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImprovedGameEventListenerRenderer implements DebugRenderer.SimpleDebugRenderer {
	private final Minecraft minecraft;
	private final List<TrackedGameEvent> trackedGameEvents = Lists.newArrayList();
	public final List<TrackedListener> trackedListeners = Lists.newArrayList();

	public ImprovedGameEventListenerRenderer(Minecraft client) {
		this.minecraft = client;
	}

	public void tick() {
		this.trackedListeners.removeIf(listener -> listener.isExpired(this.minecraft.level));
		this.trackedListeners.forEach(
			trackedListener -> {
				trackedListener.attemptResolveEntity(this.minecraft.level);
				trackedListener.attemptResolveBlock(this.minecraft.level);
			}
		);
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		Level level = this.minecraft.level;
		if (level == null) {
			this.trackedGameEvents.clear();
			this.trackedListeners.clear();
		} else {
			Vec3 cameraPos = new Vec3(cameraX, cameraY, cameraZ);
			ArrayList<TrackedListener> listenersToRender = new ArrayList<>(this.trackedListeners);
			listenersToRender.removeIf(listener -> !listener.canRender(level, cameraPos));
			this.trackedGameEvents.removeIf(TrackedGameEvent::isExpired);
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.lines());

			for (TrackedListener trackedListener : listenersToRender) {
				trackedListener.getRenderPosition(level, FrozenClient.PARTIAL_TICK)
					.ifPresent(
						pos -> {
							double gx = pos.x() - (double)trackedListener.getListenerRadius();
							double hx = pos.y() - (double)trackedListener.getListenerRadius();
							double ix = pos.z() - (double)trackedListener.getListenerRadius();
							double jx = pos.x() + (double)trackedListener.getListenerRadius();
							double k = pos.y() + (double)trackedListener.getListenerRadius();
							double l = pos.z() + (double)trackedListener.getListenerRadius();
							LevelRenderer.renderVoxelShape(
								matrices, vertexConsumer, Shapes.create(new AABB(gx, hx, ix, jx, k, l)), -cameraX, -cameraY, -cameraZ, 1.0F, 1.0F, 0.0F, 0.35F, true
							);
						}
					);
			}

			VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderType.debugFilledBox());

			for (TrackedListener trackedListener2 : listenersToRender) {
				trackedListener2.getRenderPosition(level, FrozenClient.PARTIAL_TICK)
					.ifPresent(
						pos -> LevelRenderer.addChainedFilledBoxVertices(
							matrices,
							vertexConsumer2,
							pos.x() - 0.25 - cameraX,
							pos.y() - cameraY,
							pos.z() - 0.25 - cameraZ,
							pos.x() + 0.25 - cameraX,
							pos.y() - cameraY + 1.0,
							pos.z() + 0.25 - cameraZ,
							1.0F,
							1.0F,
							0.0F,
							0.35F
						)
					);
			}

			for (TrackedListener trackedListener2 : listenersToRender) {
				trackedListener2.getRenderPosition(level, FrozenClient.PARTIAL_TICK).ifPresent(pos -> {
					DebugRenderer.renderFloatingText(matrices, vertexConsumers, "Listener Origin", pos.x(), pos.y() + 1.8F, pos.z(), -1, 0.025F);
					DebugRenderer.renderFloatingText(matrices, vertexConsumers, BlockPos.containing(pos).toString(), pos.x(), pos.y() + 1.5, pos.z(), -6959665, 0.025F);
				});
			}

			for (TrackedGameEvent trackedGameEvent : this.trackedGameEvents) {
				Vec3 vec32 = trackedGameEvent.position;
				double e = vec32.x - 0.2F;
				double f = vec32.y - 0.2F;
				double g = vec32.z - 0.2F;
				double h = vec32.x + 0.2F;
				double i = vec32.y + 0.2F + 0.5;
				double j = vec32.z + 0.2F;
				renderFilledBox(matrices, vertexConsumers, new AABB(e, f, g, h, i, j), 1.0F, 1.0F, 1.0F, 0.2F);
				DebugRenderer.renderFloatingText(
					matrices, vertexConsumers, trackedGameEvent.gameEvent.location().toString(), vec32.x, vec32.y + 0.85F, vec32.z, -7564911, 0.0075F
				);
			}
		}
	}

	private static void renderFilledBox(PoseStack matrices, MultiBufferSource vertexConsumers, AABB box, float x, float y, float z, float color) {
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		if (camera.isInitialized()) {
			Vec3 vec3 = camera.getPosition().reverse();
			DebugRenderer.renderFilledBox(matrices, vertexConsumers, box.move(vec3), x, y, z, color);
		}
	}

	public void trackGameEvent(ResourceKey<GameEvent> registryKey, Vec3 pos) {
		this.trackedGameEvents.add(new TrackedGameEvent(Util.getMillis(), registryKey, pos));
	}

	public void trackListener(PositionSource positionSource, int range) {
		this.trackedListeners.add(new TrackedListener(positionSource, range));
	}

	@Environment(EnvType.CLIENT)
	record TrackedGameEvent(long timeStamp, ResourceKey<GameEvent> gameEvent, Vec3 position) {
		public boolean isExpired() {
			return Util.getMillis() - this.timeStamp > 3000L;
		}
	}

	@Environment(EnvType.CLIENT)
	static class TrackedListener implements GameEventListener {
		private Entity entity;
		private ResourceLocation blockId;
		public boolean resolved = false;
		public final PositionSource listenerSource;
		public final int listenerRange;

		public TrackedListener(PositionSource positionSource, int range) {
			this.listenerSource = positionSource;
			this.listenerRange = range;
		}

		public void attemptResolveEntity(Level world) {
			if (!this.resolved && this.listenerSource instanceof EntityPositionSource entityPositionSource) {
				entityPositionSource.resolveEntity(world);
				if (entityPositionSource.entityOrUuidOrId.left().isPresent()) {
					this.entity = entityPositionSource.entityOrUuidOrId.left().get();
					this.resolved = true;
				}
			}
		}

		public void attemptResolveBlock(Level world) {
			if (!this.resolved && this.listenerSource instanceof BlockPositionSource blockPositionSource) {
				blockPositionSource.getPosition(world).ifPresent(
					vec3 -> {
						this.resolved = true;
						this.blockId = world.getBlockState(BlockPos.containing(vec3)).getBlock().builtInRegistryHolder().key().location();
					}
				);
			}
		}

		public boolean canRender(Level world, Vec3 pos) {
			return this.listenerSource.getPosition(world).filter(position -> position.distanceToSqr(pos) <= 1024D).isPresent();
		}

		public boolean isExpired(Level world) {
			if (this.resolved) {
				if (this.listenerSource instanceof EntityPositionSource) {
					return this.entity == null || this.entity.isRemoved();
				} else {
					Optional<Vec3> optional = this.listenerSource.getPosition(world);
					if (optional.isPresent()) {
						return !world.getBlockState(BlockPos.containing(optional.get())).getBlock().builtInRegistryHolder().is(this.blockId);
					}
				}
			}
			return false;
		}

		public Optional<Vec3> getRenderPosition(Level world, float partialTick) {
			if (this.resolved && this.entity != null && this.listenerSource instanceof EntityPositionSource entityPositionSource) {
				return Optional.of(entity.getPosition(partialTick).add(0D, entityPositionSource.yOffset, 0D));
			}
			return this.listenerSource.getPosition(world);
		}

		@Override
		public @NotNull PositionSource getListenerSource() {
			return this.listenerSource;
		}

		@Override
		public int getListenerRadius() {
			return this.listenerRange;
		}

		@Override
		public boolean handleGameEvent(ServerLevel world, Holder<GameEvent> gameEvent, GameEvent.Context context, Vec3 pos) {
			return false;
		}
	}
}
