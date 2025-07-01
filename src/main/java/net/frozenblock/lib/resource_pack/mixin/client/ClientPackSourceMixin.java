package net.frozenblock.lib.resource_pack.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(FolderRepositorySource.class)
public class ClientPackSourceMixin {

	@Unique
	private static final PackSelectionConfig FROZENLIB$FROZENLIB_PACK_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);

	@WrapOperation(
		method = "method_45272",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/packs/repository/Pack;readMetaAndCreate(Lnet/minecraft/server/packs/PackLocationInfo;Lnet/minecraft/server/packs/repository/Pack$ResourcesSupplier;Lnet/minecraft/server/packs/PackType;Lnet/minecraft/server/packs/PackSelectionConfig;)Lnet/minecraft/server/packs/repository/Pack;"
		)
	)
	public Pack frozenLib$forceEnableFrozenLibPacks(
		PackLocationInfo packLocationInfo, Pack.ResourcesSupplier resourcesSupplier, PackType packType, PackSelectionConfig packSelectionConfig, Operation<Pack> original
	) {
		if (packLocationInfo.id().startsWith("frozenlib/file/")) packSelectionConfig = FROZENLIB$FROZENLIB_PACK_SELECTION_CONFIG;
		return original.call(packLocationInfo, resourcesSupplier, packType, packSelectionConfig);
	}

}
