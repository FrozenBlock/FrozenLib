package net.frozenblock.lib.core.mixin;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.impl.registry.sync.RemapStateImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemapStateImpl.class)
public class RemapStateImplMixin {

	@Shadow
	@Final
	private Int2ObjectMap<ResourceLocation> oldIdMap;

	@Shadow
	@Final
	private Int2ObjectMap<ResourceLocation> newIdMap;

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	public void onInit(Registry registry, Int2ObjectMap oldIdMap, Int2IntMap rawIdChangeMap, CallbackInfo ci) {
		this.oldIdMap.forEach((oldInt, oldId) -> {
			this.newIdMap.forEach((newInt, newId) -> {
				if (newId.equals(oldId)) {
					System.out.println("(RemapStateImpl) " + registry.key().location().getPath() + ": " + newId + " - " + "remapped: " + newInt + " old: " + oldInt);
				}
			});
		});
	}
}
