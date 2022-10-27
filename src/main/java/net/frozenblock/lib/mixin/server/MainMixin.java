package net.frozenblock.lib.mixin.server;

import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.entrypoints.DataInitEntrypoint;
import net.minecraft.WorldVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.nio.file.Path;
import java.util.Collection;

@Mixin(Main.class)
public class MainMixin {

	@Inject(method = "createStandardGenerator", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/DataGenerator;getBuiltinDatapack(ZLjava/lang/String;)Lnet/minecraft/data/DataGenerator$PackGenerator;", ordinal = 0))
	private static void dataInit(Path output, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate, WorldVersion worldVersion, boolean bl, CallbackInfoReturnable<DataGenerator> cir) {
		FabricLoader.getInstance().getEntrypointContainers("frozenlib:data_init", DataInitEntrypoint.class).forEach(entrypoint -> {
			try {
				DataInitEntrypoint mainPoint = entrypoint.getEntrypoint();
				mainPoint.init();
			} catch (Throwable ignored) {

			}
		});
	}
}
