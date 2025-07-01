package net.frozenblock.lib.resource_pack.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.resource_pack.impl.FrozenLibFolderRepositorySource;
import net.frozenblock.lib.resource_pack.impl.PackRepositoryInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.io.File;
import java.nio.file.Path;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	@Final
	public File gameDirectory;

	@Shadow
	@Final
	private DirectoryValidator directoryValidator;

	@ModifyExpressionValue(
		method = "<init>",
		at = @At(
			value = "NEW",
			target = "([Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;"
		)
	)
	public PackRepository frozenLib$addFrozenLibRepositorySource(PackRepository original) {
		Path frozenLibResourcePackPath = this.gameDirectory.toPath().resolve("frozenlib").resolve("resourcepacks");
		RepositorySource frozenLibRepositorySource = new FrozenLibFolderRepositorySource(
			frozenLibResourcePackPath, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN, this.directoryValidator
		);
		if (original instanceof PackRepositoryInterface packRepositoryInterface) {
			packRepositoryInterface.frozenLib$addRepositorySource(frozenLibRepositorySource);
		} else if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			throw new AssertionError("BRUHHHH ITS NOT A FROZENLIB PACK REPOSITORY SOURCEEE BURHHHHHHGHGTY");
		}
		return original;
	}

}
