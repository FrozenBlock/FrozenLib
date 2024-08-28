package net.frozenblock.lib.debug.client;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ImprovedGoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
	private final Minecraft client;
	public final Int2ObjectMap<EntityGoalInfo> goalSelectors = new Int2ObjectLinkedOpenHashMap<>();

	@Override
	public void clear() {
		this.goalSelectors.clear();
	}

	public void addGoalSelector(
		@NotNull Entity entity,
		List<GoalDebugPayload.DebugGoal> selectors
	) {
		this.goalSelectors.put(
			entity.getId(),
			new EntityGoalInfo(entity, selectors)
		);
	}

	public void removeGoalSelector(int index) {
		this.goalSelectors.remove(index);
	}

	public ImprovedGoalSelectorDebugRenderer(Minecraft client) {
		this.client = client;
	}

	@Override
	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		Vec3 cameraPos = new Vec3(cameraX, cameraY, cameraZ);

		for (EntityGoalInfo entityGoalInfo : this.goalSelectors.values()) {
			Vec3 entityPos = entityGoalInfo.entity.getPosition(FrozenClient.PARTIAL_TICK).add(0D, entityGoalInfo.entity.getEyeHeight() + 0.5D, 0D);
			if (cameraPos.closerThan(entityPos, 160D, 256D)) {
				for (int i = 0; i < entityGoalInfo.goals.size(); i++) {
					GoalDebugPayload.DebugGoal debugGoal = entityGoalInfo.goals.get(i);
					double x = entityPos.x;
					double y = entityPos.y + (double)i * 0.25D;
					double z = entityPos.z;
					int textColor = debugGoal.isRunning() ? -16711936 : -3355444;
					DebugRenderer.renderFloatingText(matrices, vertexConsumers, debugGoal.name(), x, y, z, textColor);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	record EntityGoalInfo(Entity entity, List<GoalDebugPayload.DebugGoal> goals) {
	}
}
