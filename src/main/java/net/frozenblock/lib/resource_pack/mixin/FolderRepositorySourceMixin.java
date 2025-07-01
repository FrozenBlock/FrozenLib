package net.frozenblock.lib.resource_pack.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.lib.resource_pack.impl.FrozenLibFolderRepositorySource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Mixin(FolderRepositorySource.class)
public class FolderRepositorySourceMixin {

	@WrapOperation(
		method = "createDiscoveredFilePackInfo",
		at = @At(
			value = "NEW",
			target = "(Ljava/lang/String;Lnet/minecraft/network/chat/Component;Lnet/minecraft/server/packs/repository/PackSource;Ljava/util/Optional;)Lnet/minecraft/server/packs/PackLocationInfo;"
		)
	)
	private PackLocationInfo frozenLib$modifyPackLocationInfo(String string, Component component, PackSource packSource, Optional optional, Operation<PackLocationInfo> original) {
		if (FolderRepositorySource.class.cast(this) instanceof FrozenLibFolderRepositorySource) {
			String componentString = string;
			if (componentString.endsWith(".zip")) componentString = componentString.substring(0, componentString.length() - 4);
			if (componentString.startsWith("file/")) componentString = componentString.substring(5);
			component = Component.translatable("frozenlib.resourcepack." + componentString);

			string = "frozenlib/" + string;
		}
		return original.call(string, component, packSource, optional);
	}

}
