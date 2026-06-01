package net.frozenblock.lib.entity.mixin.cubemob.sulfurcube;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.Map;
import java.util.Optional;
import net.frozenblock.lib.entity.api.cubemob.sulfurcube.SulfurCubeEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SulfurCube.class)
public class SulfurCubeMixin {

	@ModifyExpressionValue(
		method = "mobInteract",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Bucketable;bucketMobPickup(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/Optional;"
		)
	)
	public Optional<InteractionResult> frozenLib$onSulfurCubeInteract(
		Optional<InteractionResult> original,
		Player player, InteractionHand hand
	) {
		if (original.isPresent()) return original;

		final Optional<InteractionResult> eventInteractionResult = SulfurCubeEvents.ON_INTERACT.invoker().onInteract(
			SulfurCube.class.cast(this),
			player,
			hand
		);
		if (eventInteractionResult.isPresent()) return eventInteractionResult;

		return original;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void frozenLib$checkPowerAndInvokeOnPowerEvent(CallbackInfo info) {
		final SulfurCube sulfurCube = SulfurCube.class.cast(this);
		if (!(sulfurCube.level() instanceof ServerLevel level)) return;

		final boolean wasPowered = sulfurCube.getAttachedOrElse(SulfurCubeEvents.POWERED, false);
		final boolean isPowered = level.getBestOwnOrNeighbourSignal(BlockPos.containing(sulfurCube.position())) != 0;
		sulfurCube.setAttached(SulfurCubeEvents.POWERED, isPowered);
		if (!wasPowered && isPowered) {
			SulfurCubeEvents.ON_POWER_CHANGED.invoker().onPowerChanged(sulfurCube, true);
		} else if (wasPowered && !isPowered) {
			SulfurCubeEvents.ON_POWER_CHANGED.invoker().onPowerChanged(sulfurCube, false);
		}
	}

	@Inject(
		method = "playerPush",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;playSound(Lnet/minecraft/sounds/SoundEvent;)V"
		)
	)
	private void frozenLib$onPushSoundPlayed(
		Player player, CallbackInfo info,
		@Local(name = "pushVelocity") Vec3 pushVelocity
	) {
		SulfurCubeEvents.ON_PUSH_SOUND_PLAYED.invoker().onPushSoundPlayed(SulfurCube.class.cast(this), player, pushVelocity);
	}

	@Inject(
		method = "playerPush",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;addDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
		)
	)
	private void frozenLib$onPush(
		Player player, CallbackInfo info,
		@Local(name = "pushVelocity") Vec3 pushVelocity
	) {
		SulfurCubeEvents.ON_PUSH.invoker().onPush(SulfurCube.class.cast(this), player, pushVelocity);
	}

	@Inject(
		method = "knockback",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/cubemob/SulfurCube;playSound(Lnet/minecraft/sounds/SoundEvent;)V"
		)
	)
	private void frozenLib$onHit(
		double power, double xd, double zd, DamageSource source, float damage, CallbackInfo info,
		@Local(name = "verticalPower") float verticalPower,
		@Local(name = "horizontalKnockback") Vec3 horizontalKnockback
	) {
		SulfurCubeEvents.ON_HIT.invoker().onHit(
			SulfurCube.class.cast(this),
			new Vec3(horizontalKnockback.x(), verticalPower, horizontalKnockback.z()),
			source,
			damage
		);
	}

	@Inject(
		method = "collectEquipmentChanges",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;clear()V"
		)
	)
	public void frozenLib$onArchetypeDataRemove(
		Map<EquipmentSlot, ItemStack> lastEquipmentItems, CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> info,
		@Share("frozenLib$archetypeDataStorage") LocalRef<SulfurCubeEvents.ArchetypeDataStorage> archetypeDataStorage
	) {
		archetypeDataStorage.set(new SulfurCubeEvents.ArchetypeDataStorage());
		SulfurCubeEvents.ON_ARCHETYPE_DATA_REMOVE.invoker().onArchetypeDataRemove(SulfurCube.class.cast(this), archetypeDataStorage.get());
	}

	@Inject(
		method = "collectEquipmentChanges",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/SulfurCubeArchetype;knockbackModifiers()Lnet/minecraft/world/entity/SulfurCubeArchetype$KnockbackModifiers;"
		)
	)
	public void frozenLib$onArchetypeApply(
		Map<EquipmentSlot, ItemStack> lastEquipmentItems, CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> info,
		@Local(name = "archetype") SulfurCubeArchetype archetype,
		@Share("frozenLib$archetypeDataStorage") LocalRef<SulfurCubeEvents.ArchetypeDataStorage> archetypeDataStorage
	) {
		final Holder<SulfurCubeArchetype> archetypeHolder = SulfurCube.class.cast(this)
			.registryAccess()
			.lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE)
			.wrapAsHolder(archetype);
		SulfurCubeEvents.ON_ARCHETYPE_APPLY.invoker().onArchetypeApply(SulfurCube.class.cast(this), archetypeHolder, archetypeDataStorage.get());
	}
}
